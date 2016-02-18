package com.ambassador.ambassadorsdk.internal.adapters;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.ambassador.ambassadorsdk.B;
import com.ambassador.ambassadorsdk.R;
import com.ambassador.ambassadorsdk.RAFOptions;
import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.Utilities;
import com.ambassador.ambassadorsdk.internal.dialogs.ContactInfoDialog;
import com.ambassador.ambassadorsdk.internal.models.Contact;
import com.ambassador.ambassadorsdk.internal.utils.Device;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterfork.Bind;
import butterfork.ButterFork;

public final class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ContactViewHolder> {

    private RAFOptions raf = RAFOptions.get();
    private Context context;

    private List<Contact> contacts;
    private List<Contact> filteredContacts;
    private List<Contact> selectedContacts;

    private boolean shouldShowPhoneNumbers;
    private float maxNameWidth;
    private Bitmap noPicBmp;
    private float checkmarkXPos;
    private float checkmarkSize;

    @Inject protected Device device;

    private OnSelectedContactsChangedListener onSelectedContactsChangedListener;

    public ContactListAdapter(Context context, List<Contact> contacts, boolean shouldShowPhoneNumbers) {
        this.context = context;
        this.contacts = contacts;
        this.selectedContacts = new ArrayList<>();
        this.filteredContacts = new ArrayList<>(contacts);
        this.shouldShowPhoneNumbers = shouldShowPhoneNumbers;
        this.noPicBmp = generateNoPicBitmap(context);
        this.checkmarkXPos = Utilities.getPixelSizeForDimension(R.dimen.contact_select_checkmark_x);
        this.checkmarkSize = Utilities.getPixelSizeForDimension(R.dimen.checkmark_size);

        AmbSingleton.getGraph().inject(this);
    }

    @Override
    public int getItemCount() {
        return filteredContacts.size();
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_contacts, parent, false);

        ContactViewHolder cvh = new ContactViewHolder(v, new ContactViewHolder.OnContactClickListener() {
            @Override
            public void onClick(View view, int position) {
                updateArrays(view, position);
                if (onSelectedContactsChangedListener != null) {
                    onSelectedContactsChangedListener.onSelectedContactsChanged(selectedContacts.size());
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                ContactInfoDialog cid = new ContactInfoDialog(context);
                cid.setCancelable(true);
                cid.setCanceledOnTouchOutside(true);
                cid.show();
                cid.setContactObject(filteredContacts.get(position), shouldShowPhoneNumbers);
            }
        });

        this.maxNameWidth = Utilities.getDpSizeForPixels(cvh.tvName.getMaxWidth());
        return cvh;
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        Contact contact = filteredContacts.get(position);

        float widthInDp = Utilities.getTextWidthDp(contact.getName(), holder.tvName);

        if (widthInDp > maxNameWidth) {
            String text = Utilities.cutTextToShow(contact.getName(), holder.tvName, maxNameWidth);
            holder.tvName.setText(text);
            holder.tvDots.setVisibility(View.VISIBLE);
        } else {
            holder.tvName.setText(contact.getName());
            holder.tvDots.setVisibility(View.GONE);
        }

        if (shouldShowPhoneNumbers) {
            String text = contact.getType() + " - " + contact.getPhoneNumber();
            holder.tvNumberOrEmail.setText(text);
        } else {
            holder.tvNumberOrEmail.setText(contact.getEmailAddress());
        }

        /** Checks whether the view should be selected or not and correctly positions the checkmark image */
        if (selectedContacts.contains(filteredContacts.get(position))) {
            holder.ivCheckMark.setTranslationX(-(checkmarkSize + checkmarkXPos));
        } else {
            holder.ivCheckMark.setTranslationX(0);
        }

        if (contact.getThumbnailBitmap() != null) {
            holder.ivPic.setImageBitmap(contact.getThumbnailBitmap());
        } else if (contact.getThumbnailUri() != null) {
            new BitmapLoaderTask(holder.ivPic, contact).execute(contact.getThumbnailUri());
        } else {
            holder.ivPic.setImageBitmap(noPicBmp);
            contact.setThumbnailBitmap(noPicBmp);
        }
    }

    public void filterList(String filterString) {
        if (filterString != null && !filterString.equals("")) {
            filteredContacts.clear();
            for (int i = 0; i < contacts.size(); i++) {
                Contact object = contacts.get(i);
                String name = object.getName().toLowerCase();
                String value = (shouldShowPhoneNumbers ? object.getPhoneNumber() : object.getEmailAddress()).toLowerCase();
                String query = searchProcessedString(filterString);
                if (searchProcessedString(name).contains(query) || searchProcessedString(value).contains(query)) {
                    filteredContacts.add(object);
                }
            }

            notifyDataSetChanged();
        } else {
            filteredContacts.clear();
            filteredContacts = new ArrayList<>(contacts);
            notifyDataSetChanged();
        }
    }

    private String searchProcessedString(String raw) {
        return raw.toLowerCase().replace(" ", "").replace("-", "").replace("(", "").replace(")", "");
    }

    public void updateArrays(View view, int position) {
        final ImageView imageView = (ImageView) view.findViewById(R.id.ivCheckMark);
        if (selectedContacts.contains(filteredContacts.get(position))) {
            selectedContacts.remove(filteredContacts.get(position));
            ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "translationX", -(checkmarkSize + checkmarkXPos), 0);
            animator.setDuration(100);
            animator.start();
        } else {
            selectedContacts.add(filteredContacts.get(position));
            ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "translationX", 0, -(checkmarkSize + checkmarkXPos));
            animator.setDuration(300);
            animator.setInterpolator(new OvershootInterpolator());
            animator.start();
        }
    }

    public int getSelectedSize() {
        return selectedContacts.size();
    }

    /** Get a deep copy of the selected contacts */
    public List<Contact> getSelectedContacts() {
        List<Contact> tmp = new ArrayList<>();
        for (Contact contact : selectedContacts) tmp.add(contact.copy());
        return tmp;
    }

    private Bitmap generateNoPicBitmap(Context context) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_person_white_48dp);
        Bitmap tmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(tmp);

        Paint paint = new Paint();
        paint.setColor(raf.getContactNoPhotoAvailableBackgroundColor());
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPaint(paint);

        canvas.drawBitmap(bitmap, null, new Rect(20, 20, bitmap.getWidth() - 20, bitmap.getWidth() - 20), new Paint(Paint.ANTI_ALIAS_FLAG));

        return tmp;
    }

    private class BitmapLoaderTask extends AsyncTask<String, Void, Bitmap> {

        private final WeakReference<ImageView> imageViewWeakReference;
        private final WeakReference<Contact> contactObjectWeakReference;

        public BitmapLoaderTask(ImageView imageView, Contact contact) {
            imageViewWeakReference = new WeakReference<>(imageView);
            contactObjectWeakReference = new WeakReference<>(contact);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                return MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(params[0]));
            } catch (IOException e) {
                return null;
            } catch (OutOfMemoryError e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewWeakReference != null && bitmap != null) {
                final ImageView imageView = imageViewWeakReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                    final Contact contact = contactObjectWeakReference.get();
                    if (contact != null) {
                        contact.setThumbnailBitmap(bitmap);
                    }
                }
            } else if (imageViewWeakReference != null) {
                final ImageView imageView = imageViewWeakReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(noPicBmp);
                }
            }
        }

    }

    public interface OnSelectedContactsChangedListener {
        void onSelectedContactsChanged(int selected);
    }

    public void setOnSelectedContactsChangedListener(OnSelectedContactsChangedListener onSelectedContactsChangedListener) {
        this.onSelectedContactsChangedListener = onSelectedContactsChangedListener;
    }

    public static final class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private RAFOptions raf = RAFOptions.get();

        @Bind(B.id.tvName)              protected TextView      tvName;
        @Bind(B.id.tvDots)              protected TextView      tvDots;
        @Bind(B.id.tvNumberOrEmail)     protected TextView      tvNumberOrEmail;
        @Bind(B.id.ivCheckMark)         protected ImageView     ivCheckMark;
        @Bind(B.id.ivPic)               protected ImageView     ivPic;

        protected OnContactClickListener listener;

        private ContactViewHolder(View itemView, @Nullable OnContactClickListener listener) {
            super(itemView);
            ButterFork.bind(this, itemView);

            tvName.setTextSize(raf.getContactsListNameSize());
            tvName.setTypeface(raf.getContactsListNameFont());

            tvDots.setTextSize(raf.getContactsListNameSize());
            tvDots.setTypeface(raf.getContactsListNameFont());

            tvNumberOrEmail.setTextSize(raf.getContactsListValueSize());
            tvNumberOrEmail.setTypeface(raf.getContactsListValueFont());

            ivCheckMark.setColorFilter(raf.getContactsToolbarColor());

            this.listener = listener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (this.listener != null) {
                this.listener.onClick(v, getPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (this.listener != null) {
                this.listener.onLongClick(v, getPosition());
            }
            return true;
        }

        public interface OnContactClickListener {
            void onClick(View view, int position);
            void onLongClick(View view, int position);
        }

    }

}
