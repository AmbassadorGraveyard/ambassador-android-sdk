package com.ambassador.ambassadorsdk;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dylan on 11/19/15.
 */
class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ContactViewHolder> {

    public static class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView tvName, tvDots;
        TextView tvNumberOrEmail;
        ImageView ivCheckMark;
        ImageView ivPic;
        OnContactClickListener listener;

        ContactViewHolder(View itemView, OnContactClickListener listener) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvDots = (TextView) itemView.findViewById(R.id.tvDots);
            tvNumberOrEmail = (TextView) itemView.findViewById(R.id.tvNumberOrEmail);
            ivCheckMark = (ImageView) itemView.findViewById(R.id.ivCheckMark);
            ivPic = (ImageView) itemView.findViewById(R.id.ivPic);

            this.listener = listener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            this.listener.onClick(v, getPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            this.listener.onLongClick(v, getPosition());
            return true;
        }

        public interface OnContactClickListener {
            void onClick(View view, int position);
            void onLongClick(View view, int position);
        }

    }

    private Context context;

    private List<ContactObject> contacts, filteredContacts, selectedContacts;

    private boolean shouldShowPhoneNumbers;
    private float maxNameWidth;
    private float checkmarkXPos;
    private int itemWidth;
    private Bitmap noPicBmp;

    private OnSelectedContactsChangedListener onSelectedContactsChangedListener;

    public ContactListAdapter(Context context, List<ContactObject> contacts, boolean shouldShowPhoneNumbers) {
        this.context = context;
        this.contacts = contacts;
        this.selectedContacts = new ArrayList<>();
        this.filteredContacts = new ArrayList<>(contacts);
        this.shouldShowPhoneNumbers = shouldShowPhoneNumbers;
        this.checkmarkXPos = Utilities.getPixelSizeForDimension(R.dimen.contact_select_checkmark_x);
        this.noPicBmp = generateNoPicBitmap(context);
    }

    @Override
    public int getItemCount() {
        return filteredContacts.size();
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_contacts, parent, false);

        v.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                v.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                itemWidth = v.getWidth();
            }
        });

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

        _setOtherFields(cvh);
        return cvh;
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        ContactObject contact = filteredContacts.get(position);

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
            holder.tvNumberOrEmail.setText(contact.getType() + " - " + contact.getPhoneNumber());
        } else {
            holder.tvNumberOrEmail.setText(contact.getEmailAddress());
        }

        /** Checks whether the view should be selected or not and correctly positions the checkmark image */
        if (selectedContacts.contains(filteredContacts.get(position))) {
            holder.ivCheckMark.setX(itemWidth - holder.ivCheckMark.getWidth() - checkmarkXPos);
        } else {
            holder.ivCheckMark.setX(itemWidth);
        }

        if (contact.getThumbBmp() != null) {
            holder.ivPic.setImageBitmap(contact.getThumbBmp());
        } else if (contact.getThumbnailUri() != null) {
            new BitmapLoaderTask(holder.ivPic, contact).execute(contact.getThumbnailUri());
        } else {
            holder.ivPic.setImageBitmap(noPicBmp);
            contact.setThumbBmp(noPicBmp);
        }
    }

    private void _setOtherFields(ContactViewHolder cvh) {
        this.maxNameWidth = Utilities.getDpSizeForPixels(cvh.tvName.getMaxWidth());
    }

    public void filterList(String filterString) {
        if (filterString != null && !filterString.equals("")) {
            filteredContacts.clear();
            for (int i = 0; i < contacts.size(); i++) {
                ContactObject object = contacts.get(i);
                if (object.getName().toLowerCase().contains(filterString.toLowerCase())) { filteredContacts.add(object); }
            }

            notifyDataSetChanged();
        } else {
            filteredContacts.clear();
            filteredContacts = new ArrayList<>(contacts);
            notifyDataSetChanged();
        }
    }

    public void updateArrays(View view, int position) {
        final ImageView imageView = (ImageView) view.findViewById(R.id.ivCheckMark);
        if (selectedContacts.contains(filteredContacts.get(position))) {
            selectedContacts.remove(filteredContacts.get(position));
            imageView.animate()
                    .setDuration(100)
                    .x(view.getWidth()).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationCancel(animation);
                            imageView.setVisibility(View.GONE);
                        }
                    })
                    .start();
        } else {
            selectedContacts.add(filteredContacts.get(position));
            imageView.setVisibility(View.VISIBLE);
            imageView.animate()
                    .setDuration(300)
                    .setInterpolator(new OvershootInterpolator())
                    .x(view.getWidth() - imageView.getWidth() - checkmarkXPos)
                    .setListener(null)
                    .start();
        }
    }

    public int getSelectedSize() {
        return selectedContacts.size();
    }

    /** Get a deep copy of the selected contacts */
    public List<ContactObject> getSelectedContacts() {
        List<ContactObject> tmp = new ArrayList<>();
        for (ContactObject contact : selectedContacts) tmp.add(contact.clone());
        return tmp;
    }

    private Bitmap generateNoPicBitmap(Context context) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_person_white_48dp);
        Bitmap tmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(tmp);

        Paint paint = new Paint();
        paint.setColor(context.getResources().getColor(R.color.contactNoPhotoAvailableBackground));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPaint(paint);

        canvas.drawBitmap(bitmap, null, new Rect(0, 0, bitmap.getWidth(), bitmap.getWidth()), new Paint(Paint.ANTI_ALIAS_FLAG));

        return tmp;
    }

    private class BitmapLoaderTask extends AsyncTask<String, Void, Bitmap> {

        private final WeakReference<ImageView> imageViewWeakReference;
        private final WeakReference<ContactObject> contactObjectWeakReference;

        public BitmapLoaderTask(ImageView imageView, ContactObject contact) {
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
                    final ContactObject contact = contactObjectWeakReference.get();
                    if (contact != null) {
                        contact.setThumbBmp(bitmap);
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

}
