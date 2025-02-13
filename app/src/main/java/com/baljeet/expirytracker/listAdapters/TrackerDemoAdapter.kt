package com.baljeet.expirytracker.listAdapters

import android.content.Context
import android.graphics.drawable.LayerDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import com.baljeet.expirytracker.databinding.WidgetStackViewItemBinding
import com.baljeet.expirytracker.util.ImageConvertor
import java.time.Duration
import java.time.LocalDateTime


class TrackerDemoAdapter(private val context: Context) : ListAdapter<TrackerAndProduct, TrackerDemoAdapter.MyViewHolder>(DiffUtil()){

    class DiffUtil : androidx.recyclerview.widget.DiffUtil.ItemCallback<TrackerAndProduct>(){
        override fun areItemsTheSame(oldItem: TrackerAndProduct, newItem: TrackerAndProduct): Boolean {
            return oldItem.tracker.trackerId == newItem.tracker.trackerId
        }

        override fun areContentsTheSame(oldItem: TrackerAndProduct, newItem: TrackerAndProduct): Boolean {
            return oldItem == newItem
        }
    }

    class MyViewHolder(val bind : WidgetStackViewItemBinding) : RecyclerView.ViewHolder(bind.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ):MyViewHolder {
        return MyViewHolder(WidgetStackViewItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val tracker = getItem(position)
        holder.bind.apply {
            productName.text = tracker.productAndCategoryAndImage.product.name
            when(tracker.productAndCategoryAndImage.image.mimeType){
                "asset"->{
                    productImage.setImageDrawable(
                        AppCompatResources.getDrawable(
                            context,
                            context.resources.getIdentifier(
                                tracker.productAndCategoryAndImage.image.imageUrl,
                                "drawable",
                                context.packageName
                            )
                        )
                    )
                }
                else->{
                    productImage.setImageBitmap(
                        ImageConvertor.stringToBitmap(tracker.productAndCategoryAndImage.image.bitmap)
                    )
                }
            }
            val expiryDate = tracker.tracker.expiryDate
            val mfgDate = tracker.tracker.mfgDate
            expiryDate?.let {
                expiringDate.text = context.getString(R.string.date_string_with_month_name_2_lined,it.dayOfMonth,it.month.name.substring(0,3).uppercase(), it.year)
            }
            val dateToday = LocalDateTime.now()

            val totalHours = Duration.between(mfgDate,expiryDate).toMinutes()
            val spentHours = Duration.between(mfgDate,dateToday).toMinutes()


            val progressValue =
                (spentHours.toFloat() / totalHours.toFloat()) * 100
            itemProgressbar.apply {
                val layerDrawable = progressDrawable as LayerDrawable
                val progressDrawableClip = layerDrawable.getDrawable(1)
                when {
                    progressValue >= 100->{
                        progressDrawableClip.setTint(context.getColor(R.color.progress_bad))
                        status.text = context.getText(R.string.expired)
                    }
                    progressValue >= 80 -> {
                        progressDrawableClip.setTint(context.getColor(R.color.progress_bad))
                        status.text = context.getText(R.string.still_ok)
                    }
                    progressValue < 80 && progressValue >= 50 -> {
                        progressDrawableClip.setTint(context.getColor(R.color.progress_ok))
                        status.text = context.getText(R.string.good)
                    }
                    progressValue < 50 -> {
                        progressDrawableClip.setTint(context.getColor(R.color.progress_great))
                        status.text = context.getText(R.string.fresh)
                    }
                }
            }
            itemProgressbar.progress  = progressValue.toInt()
        }
    }
}