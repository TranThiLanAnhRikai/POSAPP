package com.example.pos
import android.content.ContentValues.TAG
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.pos.data.repository.OrderRepository
import com.example.pos.model.MainMenuViewModel
import com.example.pos.model.MainMenuViewModelFactory
import com.example.pos.model.SalesViewModel
import com.example.pos.model.SalesViewModelFactory
import com.example.pos_admin.data.PosAdminRoomDatabase
import com.example.pos_admin.databinding.FragmentMainMenuBinding
import com.example.pos_admin.databinding.FragmentSalesAnalysisBinding
import com.naqdi.chart.model.Line

/**
 * A simple [Fragment] subclass.
 * Use the [SalesAnalysisFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SalesAnalysisFragment : Fragment() {
    private var binding: FragmentSalesAnalysisBinding? = null
    private val salesViewModel: SalesViewModel by activityViewModels {
        SalesViewModelFactory(
            OrderRepository(
                PosAdminRoomDatabase.getDatabase(requireContext()).orderDao())
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentSalesAnalysisBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.salesAnalysisFragment = this
        binding?.salesViewModel = salesViewModel
        salesViewModel.getLatestOrders().observe(viewLifecycleOwner, Observer { orders ->
            salesViewModel.numberOfOrders.clear()
            salesViewModel.revenueList.clear()
            salesViewModel.numberOfItems.clear()
            salesViewModel.dessertRevenueList.clear()
            salesViewModel.drinkRevenueList.clear()
            salesViewModel.foodRevenueList.clear()
            orders.forEach { order ->
                salesViewModel.dates.add(order.orderNumber.toString().substring(6, 8))
            }
            val datesList = salesViewModel.dates.distinct()
            Log.d(TAG, "dateList $datesList")
            datesList.forEach { date ->
                Log.d(TAG, "date $date")
                var sum = 0.0
                var orderSum = 0
                var quantitySum = 0
                var foodSales = 0.0
                var drinkSales = 0.0
                var dessertSales = 0.0
                orders.filter { order ->
                    Log.d(TAG, "order $order")
                    order.orderNumber.toString().substring(6,8) == date

                }
                    .forEach { order ->
                        orderSum++
                        quantitySum += order.quantity
                        sum += order.total
                        foodSales += order.foodRevenue ?: 0.0
                        drinkSales += order.drinkRevenue ?: 0.0
                        dessertSales += order.dessertRevenue ?: 0.0
                    }

                salesViewModel.numberOfOrders.add(orderSum.toFloat())
                salesViewModel.revenueList.add(sum)
                salesViewModel.numberOfItems.add(quantitySum)
                salesViewModel.foodRevenueList.add(foodSales)
                salesViewModel.drinkRevenueList.add(drinkSales)
                salesViewModel.dessertRevenueList.add(dessertSales)
            }
            Log.d(TAG, "orders ${salesViewModel.numberOfOrders}, ${salesViewModel.revenueList}, ${salesViewModel.numberOfItems}")
            val numberOfOrders: List<Float> = salesViewModel.numberOfOrders.reversed()
            val intervalList: List<String> = salesViewModel.dates.distinct().reversed()
            val rangeList = listOf("1-100", "100-200", "200-300")
            val mutableList: List<Double> = salesViewModel.revenueList
            val revenueFloatList: List<Float> = mutableList.map{it.toFloat()}
            val foodRevenueFloatList: List<Float> = salesViewModel.foodRevenueList.map { it.toFloat() }
            val drinkRevenueFloatList: List<Float> = salesViewModel.drinkRevenueList.map { it.toFloat() }
            val dessertRevenueFloatList: List<Float> = salesViewModel.dessertRevenueList.map { it.toFloat() }
            val itemsFloatList: List<Float> = (salesViewModel.numberOfItems.map { it.toFloat() }).reversed()
            val lineList = arrayListOf<Line>().apply {
                add(Line("Total revenue", Color.BLUE, revenueFloatList.reversed()))
                add(Line("Food", Color.RED, foodRevenueFloatList))
                add(Line("Drink", Color.GRAY, drinkRevenueFloatList))
                add(Line("Dessert", Color.CYAN, dessertRevenueFloatList))
            }
            binding?.chainChartView?.setData(lineList, intervalList, rangeList)
            binding?.chainChartView?.apply {
                setLineSize(5f)
                setTextSize(20f)
                setTextColor(Color.BLACK)
                setNodeSize(5F)
            }
        })


    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}