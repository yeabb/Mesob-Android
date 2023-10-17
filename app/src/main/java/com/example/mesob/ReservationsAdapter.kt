package com.example.mesob

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView

class ReservationsAdapter(private var foodMenuArr: ArrayList<FoodMenuData>):

    RecyclerView.Adapter<ReservationsAdapter.ReservationsViewHolder>() {

    var onItemClickListener: ((FoodMenuData) -> Unit)? = null

    inner class ReservationsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val siFoodImage: ShapeableImageView = itemView.findViewById(R.id.siFoodImage)
        val tvFoodName: TextView = itemView.findViewById(R.id.tvFoodName)
        val tvRestaurantName: TextView = itemView.findViewById(R.id.tvRestaurantName)




        fun bind(foodMenu: FoodMenuData) {
            siFoodImage.setImageResource(R.drawable.kitfo)
            tvFoodName.text = foodMenu.foodName
            tvRestaurantName.text = foodMenu.restaurantName

            itemView.setOnClickListener {
                onItemClickListener?.invoke(foodMenu)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_reservation, parent, false)
        return ReservationsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return foodMenuArr.size
    }

    override fun onBindViewHolder(holder: ReservationsViewHolder, position: Int) {
        val currentItem = foodMenuArr[position]
        holder.siFoodImage.setImageResource(R.drawable.kitfo)
        holder.tvFoodName.text = currentItem.foodName
        holder.tvRestaurantName.text = currentItem.restaurantName


        holder.bind(currentItem)
    }
}
