package io.kazutoiris.infinite.illusion

import android.content.Context
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit

@InjectYukiHookWithXposed
object HookEntry : IYukiHookXposedInit {
    private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    private fun generateRandomPackageName(): String {
        val prefix = listOf("com", "io", "net", "org").random()
        val segments = (2..4).random()  // 生成2-4段层级
        return buildString {
            append(prefix)
            repeat(segments) {
                append(".${
                    (6..12).random().let { length ->
                        (1..length).joinToString("") { charPool.random().toString() }
                    }
                }")
            }
        }
    }

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

                    appClassLoader = app?.javaClass?.method {
                        name = "getClassLoader"
                    }?.get(app)?.invoke() as ClassLoader?

                    "ႎ.Ϳ".toClass().method {
                        name = "getBody"
                    }.hook {
                        after {
                            try {
                                result!!::class.java.getDeclaredField("disabledApps").set(
                                    result,
                                    MutableList(20) { "io.kazutoiris.infinite.illusion" })
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
                                result!!::class.java.getDeclaredField("disabledApps").set(
                                    result,
                                    MutableList(20) { "io.kazutoiris.infinite.illusion" })
                            } catch (ignored: Exception) {
                            }
                        }
                    }

                }
            }
        }

        loadApp("dev.lerist.fakelocation") {
            "android.app.ActivityThread".toClass().method {
                name = "performLaunchActivity"
            }.hook {
                before {
                    val app = instanceClass?.field {
                        name = "mInitialApplication"
                    }?.get(instance)?.cast<Context>()

                    appClassLoader = app?.javaClass?.method {
                        name = "getClassLoader"
                    }?.get(app)?.invoke() as ClassLoader?

                    "ˈ.Ϳ".toClass().method {
                        name = "getBody"
                    }.hook {
                        after {
                            try {
                                result!!::class.java.getDeclaredField("disabledApps").get(result)
                                    ?.let { it as MutableList<String> }?.apply {
                                        replaceAll {
                                            generateRandomPackageName()
                                        }
                                    }
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
                                (result!!::class.java.getDeclaredField("disabledApps")
                                    .get(result))?.let { it as MutableList<String> }?.apply {
                                        replaceAll {
                                            generateRandomPackageName()
                                        }
                                    }
                            } catch (ignored: Exception) {
                            }
                        }
                    }
                }
            }
        }
    }
}