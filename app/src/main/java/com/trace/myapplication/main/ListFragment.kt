package com.trace.myapplication.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.trace.myapplication.R
import com.trace.myapplication.Server.RequestToServer
import com.trace.myapplication.databinding.FragmentListBinding
import com.trace.myapplication.main.ListRecyclerView.ListPageAdapter
import com.trace.myapplication.main.ListRecyclerView.ListPageData
import com.trace.myapplication.main.dataType.ResponseMainList
import com.trace.myapplication.main.mainRecyclerview.MainListData
import com.trace.myapplication.startpage.myjwt
import kotlinx.android.synthetic.main.fragment_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


class ListFragment : Fragment() {
    private lateinit var binding: FragmentListBinding
    lateinit var listPageAdapter: ListPageAdapter
    var datas = mutableListOf<ListPageData>()
    val requestToServer=RequestToServer

    //nav 도착하는 쪽 코드
    val args: ListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListBinding.inflate(inflater, container, false)
        val view = binding.root
        // Inflate the layout for this fragment
        var tmptitle="건물정보-"
        tmptitle += args.title
        binding.tvListTitle.text=tmptitle

        //Log.d("도착","$args")
        binding.listBtnSearch.setOnClickListener {
            view.findNavController().navigate(R.id.reviewFragment)
        }


        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //앞 fragment에서 addextra기능
        //tv_list_fragment.text=args.text
        listPageAdapter= ListPageAdapter(view.context)
        binding.rvListPage.adapter=listPageAdapter
        loadDatas()



    }

    private fun loadDatas(){
        var tmpjwt:String= "Bearer "+ myjwt
        var tmpaddress: String
        var tmpstar=4
        Log.d("loadDatas", "함수진입+ ${tmpjwt}")
        datas = mutableListOf<ListPageData>()
        var tmppath=args.text
        Log.d("path:", "$tmppath")

        requestToServer.service.listPageRequest(
                "${tmpjwt}", "${tmppath}"
        ).enqueue(object : Callback<ResponseMainList> {
            override fun onFailure(call: Call<ResponseMainList>, t: Throwable) {
                Log.d("통신 실패", "${t.message }")
            }

            override fun onResponse(
                    call: Call<ResponseMainList>,
                    response: Response<ResponseMainList>
            ) {
                if (response.isSuccessful) {
                    if (response.body()!!.success) {
                        Log.d("성공", response.body()!!.data.toString())
                        for (i in 0 until response.body()!!.data!!.content.size){
                            tmpaddress=response.body()!!.data!!.content[i].address
                            tmpaddress += response.body()!!.data!!.content[i].lotNumber

                            datas.apply{
                                add(ListPageData(
                                        address = tmpaddress,
                                        star = tmpstar
                                ))
                            }

                        }
                        listPageAdapter.datas=datas
                        listPageAdapter.notifyDataSetChanged()
                    } else {
                        Log.d("실패", "실패")
                    }
                }
            }
        })


    }
    /*
    private fun loadDatas(){
        datas = mutableListOf<ListPageData>()
        datas.apply {
            add(
                ListPageData(
                    address = "성북구 성북동 1가 범진빌리지a",
                    star = 4
                )
            )
            add(
                ListPageData(
                    address = "성북구 성북동 1가 범진빌리지b",
                    star = 2
                )
            )
        }
        listPageAdapter.datas=datas
        listPageAdapter.notifyDataSetChanged()
    }

     */
}