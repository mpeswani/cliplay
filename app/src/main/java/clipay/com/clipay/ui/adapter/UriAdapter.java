//package clipay.com.clipay.ui.adapter;
//
//import android.net.Uri;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import java.util.List;
//
//import androidx.recyclerview.widget.RecyclerView;
//import clipay.com.clipay.R;
//
//public  class UriAdapter extends RecyclerView.Adapter<UriAdapter.UriViewHolder> {
//
//        private List<Uri> mUris;
//        private List<String> mPaths;
//
//        void setData(List<Uri> uris, List<String> paths) {
//            mUris = uris;
//            mPaths = paths;
//            notifyDataSetChanged();
//        }
//
//        @Override
//        public UriViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            return new UriViewHolder(
//                    LayoutInflater.from(parent.getContext()).inflate(R.layout.uri_item, parent, false));
//        }
//
//        @Override
//        public void onBindViewHolder(UriViewHolder holder, int position) {
//            holder.mUri.setText(mUris.get(position).toString());
//            holder.mPath.setText(mPaths.get(position));
//
//            holder.mUri.setAlpha(position % 2 == 0 ? 1.0f : 0.54f);
//            holder.mPath.setAlpha(position % 2 == 0 ? 1.0f : 0.54f);
//        }
//
//        @Override
//        public int getItemCount() {
//            return mUris == null ? 0 : mUris.size();
//        }
//
//        static class UriViewHolder extends RecyclerView.ViewHolder {
//
//            private TextView mUri;
//            private TextView mPath;
//
//            UriViewHolder(View contentView) {
//                super(contentView);
//                mUri = contentView.findViewById(R.id.uri);
//                mPath = contentView.findViewById(R.id.path);
//            }
//        }
//    }