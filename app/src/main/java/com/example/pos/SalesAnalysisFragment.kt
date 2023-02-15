package com.example.pos
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.pos.data.repository.OrderRepository
import com.example.pos.helper.CommonAdminHeaderHelper
import com.example.pos.model.SalesViewModel
import com.example.pos.model.SalesViewModelFactory
import com.example.pos_admin.R
import com.example.pos_admin.data.PosAdminRoomDatabase
import com.example.pos_admin.databinding.AdminCommonHeaderBinding
import com.example.pos_admin.databinding.FragmentSalesAnalysisBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet


/**
 *　セールはグラフで表示する
 * 週と月の二つのオプション
 */
class SalesAnalysisFragment : Fragment() {
    private var binding: FragmentSalesAnalysisBinding? = null
    private lateinit var lineChart: LineChart
    private val salesViewModel: SalesViewModel by activityViewModels {
        SalesViewModelFactory(
            OrderRepository(
                PosAdminRoomDatabase.getDatabase(requireContext()).orderDao()
            )
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
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        lineChart = binding?.lineChart!!
        salesViewModel.getOrdersByWeek().observe(viewLifecycleOwner) { orders ->
            salesViewModel.numberOfOrders.clear()
            salesViewModel.revenueList.clear()
            salesViewModel.numberOfItems.clear()
            salesViewModel.dessertRevenueList.clear()
            salesViewModel.drinkRevenueList.clear()
            salesViewModel.foodRevenueList.clear()
            salesViewModel.dates.clear()
            orders.forEach { order ->
                salesViewModel.dates.add(order.orderNumber.toString().substring(6, 8))
            }
            val datesList = salesViewModel.dates.distinct()
            datesList.forEach { date ->
                var sum = 0.0
                var orderSum = 0
                var quantitySum = 0
                var foodSales = 0.0
                var drinkSales = 0.0
                var dessertSales = 0.0
                orders.filter { order ->
                    order.orderNumber.toString().substring(6, 8) == date

                }
                    .forEach { order ->
                        orderSum++
                        quantitySum += order.quantity
                        sum += order.total
                        foodSales += order.foodRevenue ?: 0.0
                        drinkSales += order.drinkRevenue ?: 0.0
                        dessertSales += order.dessertRevenue ?: 0.0
                    }

                salesViewModel.numberOfOrders.add(orderSum)
                salesViewModel.revenueList.add(sum)
                salesViewModel.numberOfItems.add(quantitySum)
                salesViewModel.foodRevenueList.add(foodSales)
                salesViewModel.drinkRevenueList.add(drinkSales)
                salesViewModel.dessertRevenueList.add(dessertSales)
            }
            populateLineChart(
                salesViewModel.dates.distinct(),
                salesViewModel.revenueList,
                salesViewModel.foodRevenueList,
                salesViewModel.drinkRevenueList,
                salesViewModel.dessertRevenueList)
        }
            binding?.btnsContainer?.forEach { it ->
            it.setOnClickListener {
                if (it.tag.toString() == "week") {
                    salesViewModel.getOrdersByWeek().observe(viewLifecycleOwner) { orders ->
                        salesViewModel.numberOfOrders.clear()
                        salesViewModel.revenueList.clear()
                        salesViewModel.numberOfItems.clear()
                        salesViewModel.dessertRevenueList.clear()
                        salesViewModel.drinkRevenueList.clear()
                        salesViewModel.foodRevenueList.clear()
                        salesViewModel.dates.clear()
                        orders.forEach { order ->
                            salesViewModel.dates.add(order.orderNumber.toString().substring(6, 8))
                        }
                        val datesList = salesViewModel.dates.distinct()
                        datesList.forEach { date ->
                            var sum = 0.0
                            var orderSum = 0
                            var quantitySum = 0
                            var foodSales = 0.0
                            var drinkSales = 0.0
                            var dessertSales = 0.0
                            orders.filter { order ->
                                order.orderNumber.toString().substring(6, 8) == date

                            }
                                .forEach { order ->
                                    orderSum++
                                    quantitySum += order.quantity
                                    sum += order.total
                                    foodSales += order.foodRevenue ?: 0.0
                                    drinkSales += order.drinkRevenue ?: 0.0
                                    dessertSales += order.dessertRevenue ?: 0.0
                                }

                            salesViewModel.numberOfOrders.add(orderSum)
                            salesViewModel.revenueList.add(sum)
                            salesViewModel.numberOfItems.add(quantitySum)
                            salesViewModel.foodRevenueList.add(foodSales)
                            salesViewModel.drinkRevenueList.add(drinkSales)
                            salesViewModel.dessertRevenueList.add(dessertSales)
                        }
                        populateLineChart(
                            datesList,
                            salesViewModel.revenueList,
                            salesViewModel.foodRevenueList,
                            salesViewModel.drinkRevenueList,
                            salesViewModel.dessertRevenueList
                        )
                    }
                } else {
                    binding?.monthSelected?.setOnClickListener {
                        salesViewModel.getOrdersByMonth().observe(viewLifecycleOwner) { orders ->
                            salesViewModel.numberOfOrders.clear()
                            salesViewModel.revenueList.clear()
                            salesViewModel.numberOfItems.clear()
                            salesViewModel.dessertRevenueList.clear()
                            salesViewModel.drinkRevenueList.clear()
                            salesViewModel.foodRevenueList.clear()
                            orders.forEach { order ->
                                salesViewModel.months.add(
                                    order.orderNumber.toString().substring(4, 6)
                                )
                            }
                            val monthsList = salesViewModel.months.distinct()
                            monthsList.forEach { date ->
                                var sum = 0.0
                                var orderSum = 0
                                var quantitySum = 0
                                var foodSales = 0.0
                                var drinkSales = 0.0
                                var dessertSales = 0.0
                                orders.filter { order ->
                                    order.orderNumber.toString().substring(4, 6) == date

                                }
                                    .forEach { order ->
                                        orderSum++
                                        quantitySum += order.quantity
                                        sum += order.total
                                        foodSales += order.foodRevenue ?: 0.0
                                        drinkSales += order.drinkRevenue ?: 0.0
                                        dessertSales += order.dessertRevenue ?: 0.0
                                    }

                                salesViewModel.numberOfOrders.add(orderSum)
                                salesViewModel.revenueList.add(sum)
                                salesViewModel.numberOfItems.add(quantitySum)
                                salesViewModel.foodRevenueList.add(foodSales)
                                salesViewModel.drinkRevenueList.add(drinkSales)
                                salesViewModel.dessertRevenueList.add(dessertSales)
                            }
                            populateLineChart(
                                monthsList,
                                salesViewModel.revenueList,
                                salesViewModel.foodRevenueList,
                                salesViewModel.drinkRevenueList,
                                salesViewModel.dessertRevenueList
                            )
                        }
                    }
                }
            }

        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


    fun populateLineChart(
        dates: List<String>,
        totalRevenue: List<Double>,
        foodRevenue: List<Double>,
        drinkRevenue: List<Double>,
        dessertRevenue: List<Double>
    ) {
        val xvalue = ArrayList<String>()
        dates.forEach {
            xvalue.add(it)
        }
        val revenueLineEntry: ArrayList<Entry> = ArrayList()

        var i = 0
        for (entry in totalRevenue) {
            var value = totalRevenue[i].toFloat()
            revenueLineEntry.add(Entry(value, i))
            i++
        }
        val revenueLineDataSet = LineDataSet(revenueLineEntry, "Total")
        revenueLineDataSet.color = resources.getColor(R.color.dark_blue)

        val foodLineEntry: ArrayList<Entry> = ArrayList()

        var j = 0
        for (entry in foodRevenue) {
            var value = foodRevenue[j].toFloat()
            foodLineEntry.add(Entry(value, j))
            j++
        }
        val foodLineDataSet = LineDataSet(foodLineEntry, "Food")
        foodLineDataSet.color = resources.getColor(R.color.black)

        val drinkLineEntry: ArrayList<Entry> = ArrayList()

        var k = 0
        for (entry in drinkRevenue) {
            var value = drinkRevenue[k].toFloat()
            drinkLineEntry.add(Entry(value, k))
            k++
        }
        val drinkLineDataSet = LineDataSet(drinkLineEntry, "Drink")
        drinkLineDataSet.color = resources.getColor(R.color.yellow)

        val dessertLineEntry: ArrayList<Entry> = ArrayList()

        var m = 0
        for (entry in dessertRevenue) {
            var value = dessertRevenue[m].toFloat()
            dessertLineEntry.add(Entry(value, m))
            m++
        }
        val dessertLineDataSet = LineDataSet(dessertLineEntry, "Dessert")
        dessertLineDataSet.color = resources.getColor(R.color.purple_700)

        val finalDataset = ArrayList<LineDataSet>()
        finalDataset.add(revenueLineDataSet)
        finalDataset.add(foodLineDataSet)
        finalDataset.add(drinkLineDataSet)
        finalDataset.add(dessertLineDataSet)
        val data = LineData(xvalue, finalDataset as List<ILineDataSet>?)
        lineChart.data = data
        lineChart.animateXY(1000, 1000)
        revenueLineDataSet.lineWidth = 3f
        revenueLineDataSet.valueTextSize = 20f
        foodLineDataSet.lineWidth = 3f
        foodLineDataSet.valueTextSize = 20f
        drinkLineDataSet.lineWidth = 3f
        drinkLineDataSet.valueTextSize = 20f
        dessertLineDataSet.lineWidth = 3f
        dessertLineDataSet.valueTextSize = 20f
        lineChart.legend.textSize = 20f
        lineChart.legend.position = Legend.LegendPosition.ABOVE_CHART_CENTER
        lineChart.xAxis.textSize = 20f
        lineChart.axisRight.textSize = 20f
        lineChart.axisLeft.textSize = 20f
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.extraBottomOffset = 20f
        lineChart.legend.formToTextSpace = 1f
        lineChart.xAxis.labelRotationAngle = 45f
        lineChart.xAxis.setDrawGridLines(false)
        lineChart.axisLeft.xOffset = 15f
        lineChart.axisRight.xOffset = 15f
        lineChart.invalidate()
    }
}
