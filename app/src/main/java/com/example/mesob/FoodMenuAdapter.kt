package com.example.mesob

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView

class FoodMenuAdapter(private var foodMenuArr: ArrayList<FoodMenuData>):

    RecyclerView.Adapter<FoodMenuAdapter.FoodMenuViewHolder>() {

    var onItemClickListener: ((FoodMenuData) -> Unit)? = null

    inner class FoodMenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val siFoodImage: ShapeableImageView = itemView.findViewById(R.id.siFoodImage)
        val tvFoodName: TextView = itemView.findViewById(R.id.tvFoodName)
        val tvRestaurantName: TextView = itemView.findViewById(R.id.tvRestaurantName)
        val tvFoodCreditNumber: TextView = itemView.findViewById(R.id.tvFoodCreditNumber)

        fun bind(foodMenu: FoodMenuData) {
            siFoodImage.setImageResource(R.drawable.kitfo)
            tvFoodName.text = foodMenu.foodName
            tvRestaurantName.text = foodMenu.restaurantName

            // Set the queue status text based on the data
            val foodCreditNumber = foodMenu.foodCreditNumber
            tvFoodCreditNumber.text = foodCreditNumber.toString()

            itemView.setOnClickListener {
                onItemClickListener?.invoke(foodMenu)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodMenuViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_foodmenu, parent, false)
        return FoodMenuViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return foodMenuArr.size
    }

    override fun onBindViewHolder(holder: FoodMenuViewHolder, position: Int) {
        val currentItem = foodMenuArr[position]
        holder.siFoodImage.setImageResource(R.drawable.kitfo)
        holder.tvFoodName.text = currentItem.foodName
        holder.tvRestaurantName.text = currentItem.restaurantName

        // Set the queue status text based on the data
        val foodCreditNumber = currentItem.foodCreditNumber
        holder.tvFoodCreditNumber.text = foodCreditNumber.toString()

        holder.bind(currentItem)
    }
}