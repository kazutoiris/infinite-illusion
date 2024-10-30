package io.kazutoiris.infinite.illusion

import android.content.Context
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit

@InjectYukiHookWithXposed
object HookEntry : IYukiHookXposedInit {
    override fun onInit() = configs {
        isDebug = false
    }

    override fun onHook() = encase {
        loadApp("com.lerist.fakelocation") {
            "android.app.ActivityThread".toClass().method {
                name = "performLaunchActivity"
            }.hook {
                before {
                    val app = instanceClass?.field {
                        name = "mInitialApplication"
                    }?.get(instance)?.cast<Context>()

                    appClassLoader =
                        app?.javaClass?.method {
                            name = "getClassLoader"
                        }?.get(app)?.invoke() as ClassLoader?

                    "ႎ.Ϳ".toClass().method {
                        name = "getBody"
                    }.hook {
                        after {
                            try {
                                result!!::class.java.getDeclaredField("disabledApps")
                                    .set(
                                        result,
                                        MutableList(20) { "io.kazutoiris.infinite.illusion" }
                                    )
                            } catch (ignored: Exception) {
                            }
                        }
                    }

                    "com.alibaba.fastjson.JSONObject".toClass().method {
                        name = "getObject"
                        paramCount = 2
                        param(String::class.java, Class::class.java)
                    }.hook {
                        after {
                            try {
                                result!!::class.java.getDeclaredField("disabledApps")
                                    .set(
                                        result,
                                        MutableList(20) { "io.kazutoiris.infinite.illusion" }
                                    )
                            } catch (ignored: Exception) {
                            }
                        }
                    }

                }
            }
        }
    }
}