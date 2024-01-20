package com.jdm.core.base

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import java.util.concurrent.atomic.AtomicInteger

abstract class BaseFragment<T : ViewDataBinding> : Fragment() {
    @get:LayoutRes
    abstract val layoutResId: Int

    protected var backPressedTime: Long = 0
    private var _binding: T? = null
    val binding: T
        get() = _binding!!

    open var onBackPressedCallback: OnBackPressedCallback? = null

    private lateinit var requestPermission: ActivityResultLauncher<Array<String>>

    private val loadingDlgCount = AtomicInteger(0)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutResId, container, false)

        requestPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            onPermissionResult(result)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnBackPress()
        initState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    abstract fun initView()
    abstract fun initEvent()
    abstract fun subscribe()
    abstract fun initData()

    open fun initState() {
        initView()
        initEvent()
        subscribe()
        initData()
    }

    fun setOnBackPress() {
        if (onBackPressedCallback == null) {
            return
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback!!)
    }
    fun removeBackPress() {
        if (onBackPressedCallback == null) {
            return
        }
        onBackPressedCallback?.remove()
    }

    override fun onDetach() {
        onBackPressedCallback?.remove()
        onBackPressedCallback = null
        super.onDetach()
    }
    protected fun exitApp() {
        requireActivity().moveTaskToBack(true)
        requireActivity().finish()
        System.exit(0)
    }

    /**
     * 권한 요청
     */
    fun showRequestPermission(permission: Array<String>) {
        requestPermission.launch(permission)
    }

    open fun onPermissionResult(result: Map<String, Boolean>) {}
    protected fun isPermissionAllow(context: Context, permission: String): Boolean {
        return checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
    protected fun isPermissionAllow(context: Context, permission: Array<String>): Boolean {
        return permission.none { checkSelfPermission(context, it) != PackageManager.PERMISSION_DENIED }
    }
}
