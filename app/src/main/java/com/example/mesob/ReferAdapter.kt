package com.example.mesob

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView

class ReferAdapter (private var rewardArr: ArrayList<RewardData>):

    RecyclerView.Adapter<ReferAdapter.ReferViewHolder>() {

    var onItemClickListener: ((RewardData) -> Unit)? = null

    inner class ReferViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val siRewardImage: ShapeableImageView = itemView.findViewById(R.id.siRewardImage)
        val tvRewardName: TextView = itemView.findViewById(R.id.tvRewardName)
        val btRewardClaim: Button = itemView.findViewById(R.id.btRewardClaim)
        val tvRewardRequiredNumReferal: TextView = itemView.findViewById(R.id.tvRewardRequiredNumReferal)



        fun bind(reward: RewardData) {
            val rewardIdentifier = reward.RewardIdentifier

            var imageName: String? = null  // Initialize imageName with no value

            if (rewardIdentifier == 1){
                imageName = "ic_jacket"
            } else if (rewardIdentifier == 2){
                imageName = "ic_jordans"
            } else if (rewardIdentifier == 3){
                imageName = "ic_airpods"
            } else if (rewardIdentifier == 4){
                imageName = "ic_iphone"
            } else {
                imageName = "ic_macbook"
            }

            // Assuming context is available, we can use it to set the image resource dynamically
            val resourceId = itemView.context.resources.getIdentifier(imageName, "drawable", itemView.context.packageName)

            if (resourceId != 0) {
                siRewardImage.setImageResource(resourceId)
            } else {
                // Set a default image if the resource is not found
                siRewardImage.setImageResource(R.drawable.ic_refer) // Change 'default_image' to default image resource
            }

            tvRewardName.text = reward.RewardName
            tvRewardRequiredNumReferal.text = reward.RewardRequiredNumReferal.toString()

            itemView.setOnClickListener {
                onItemClickListener?.invoke(reward)
            }
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReferViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_reward, parent, false)
        return ReferViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return rewardArr.size
    }

    override fun onBindViewHolder(holder: ReferViewHolder, position: Int) {
        val currentItem = rewardArr[position]
        val rewardIdentifier = currentItem.RewardIdentifier
        var imageName: String? = null  // Initialize imageName with no value

        if (rewardIdentifier == 1){
            imageName = "ic_jacket"
        } else if (rewardIdentifier == 2){
            imageName = "ic_jordans"
        } else if (rewardIdentifier == 3){
            imageName = "ic_airpods"
        } else if (rewardIdentifier == 4){
            imageName = "ic_iphone"
        } else {
            imageName = "ic_macbook"
        }

        // Assuming context is available, we can use it to set the image resource dynamically
        val resourceId = holder.itemView.context.resources.getIdentifier(imageName, "drawable", holder.itemView.context.packageName)

        if (resourceId != 0) {
            holder.siRewardImage.setImageResource(resourceId)
        } else {
            // Set a default image if the resource is not found
            holder.siRewardImage.setImageResource(R.drawable.ic_refer) // Change 'default_image' to default image resource
        }

        holder.tvRewardName.text = currentItem.RewardName
        holder.tvRewardRequiredNumReferal.text = currentItem.RewardRequiredNumReferal.toString()

        holder.bind(currentItem)
    }




}