package webshop.konyv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class MyAdapter
        extends RecyclerView.Adapter<MyAdapter.ViewHolder>
        implements Filterable {
    private ArrayList<Book> mShoppingItemsData;
    private ArrayList<Book> mShoppingItemsDataAll;
    private Context mContext;
    private int lastPosition = -1;

    MyAdapter (Context context, ArrayList<Book> itemsData){
        mShoppingItemsData = itemsData;
        mShoppingItemsDataAll = itemsData;
        mContext = context;
    }
    private Filter shoppingFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Book> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if(constraint == null || constraint.length() == 0){
                results.count = mShoppingItemsData.size();
                results.values = mShoppingItemsDataAll;
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(Book item: mShoppingItemsDataAll){
                    if(item.getTitle().toLowerCase().contains(filterPattern) || item.getAuthor().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
                results.count = filteredList.size();
                results.values = filteredList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mShoppingItemsData = (ArrayList) results.values;
            notifyDataSetChanged();
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.books, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book currentItem = mShoppingItemsData.get(position);
        holder.bindTo(currentItem);

        /*if(holder.getAdapterPosition() > lastPosition){
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }*/
    }

    @Override
    public int getItemCount() {
        return mShoppingItemsData.size();
    }

    @Override
    public Filter getFilter() {
        return shoppingFilter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView book_img;
        private TextView titleView;
        private TextView authorView;
        private RatingBar ratingBar;
        private TextView infoView;
        private TextView price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            book_img = itemView.findViewById(R.id.book_img);
            titleView = itemView.findViewById(R.id.titleView);
            authorView = itemView.findViewById(R.id.authorView);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            infoView = itemView.findViewById(R.id.infoView);
            price = itemView.findViewById(R.id.price);
        }
        public void bindTo(Book currentItem) {
            Glide.with(mContext).load(currentItem.getImageResource()).into(book_img);
            titleView.setText(currentItem.getTitle());
            authorView.setText(currentItem.getAuthor());
            ratingBar.setRating(currentItem.getRatedInfo());
            infoView.setText(currentItem.getInfo());
            price.setText(currentItem.getPrice());

            itemView.findViewById(R.id.add_to_cart).setOnClickListener(
                    view -> ((ShoppingActivity)mContext).updateBag(currentItem)
            );
        }
    }
}
