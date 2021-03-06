package com.example.secondapp

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.secondapp.row_models.FirstRow
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.lang.Exception

class CustomAdapter(val rowList: ArrayList<FirstRow>, val context: Context): RecyclerView.Adapter<CustomAdapter.ViewHolder>() {
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name)
        val image: ImageView = itemView.findViewById(R.id.picasso_image)
        val date: TextView = itemView.findViewById(R.id.date)
        val tags: TextView = itemView.findViewById(R.id.tags)

    }

    companion object {
        const val ROW_LIST = "rowList"
        const val POSITION = "position"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomAdapter.ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.one_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = rowList.size

    override fun onBindViewHolder(holder: CustomAdapter.ViewHolder, position: Int) {
        holder.name.text = rowList[position].name
        Picasso.get().load(rowList[position].url).resize(70, 50).error(R.drawable.no_photo).into(object:Target{
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                holder.image.setImageBitmap(bitmap)
                FirebaseVision.getInstance().onDeviceImageLabeler
                    .processImage(FirebaseVisionImage.fromBitmap(bitmap!!))
                    .addOnSuccessListener {
                        val list = it.map { it.text }
                            .toTypedArray()
                            .take(3)
                        holder.tags.text = list.joinToString(", ")
                        rowList[position].tags.clear()
                        rowList[position].tags.addAll(list)
                    }
            }


        })
        holder.date.text = rowList[position].date
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ActivityWithFragments::class.java)
            intent.putParcelableArrayListExtra(ROW_LIST, rowList)
            intent.putExtra(POSITION, position)
            startActivity(context, intent, null)
        }
    }

    fun removeItem(viewHolder: RecyclerView.ViewHolder) {
        rowList.removeAt(viewHolder.adapterPosition)
        notifyItemRemoved(viewHolder.adapterPosition)
        notifyItemRangeChanged(viewHolder.adapterPosition, rowList.size-viewHolder.adapterPosition)

    }


}