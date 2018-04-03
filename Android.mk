LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

myui_sdk_dir := ../../../../vendor/ragentek/myui_sdk

res_dirs := res \
    $(myui_sdk_dir)/res

LOCAL_RESOURCE_DIR :=$(addprefix $(LOCAL_PATH)/, $(res_dirs))
LOCAL_RESOURCE_DIR +=frameworks/support/v7/appcompat/res

LOCAL_AAPT_FLAGS := \
    --auto-add-overlay \
    --extra-packages com.ragentek.myuisdk \
    --extra-packages android.support.v7.appcompat

LOCAL_MODULE_TAGS := optional

LOCAL_STATIC_JAVA_LIBRARIES := libgson222 \
				libandroidasynchttp149 \
				libhttpclient436 \
				alipaySdk-20160825 \
				android-support-v4 \
				myuisdk \
				android-support-v7-appcompat \
				wechat-sdk-android-with-mta-1.0.2
LOCAL_JAVA_LIBRARIES += mediatek-framework

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_PACKAGE_NAME := PayService

LOCAL_CERTIFICATE   := platform
LOCAL_DEX_PREOPT := false

include $(BUILD_PACKAGE)

include $(CLEAR_VARS)


LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := libgson222:libs/gson-2.2.2.jar \
					libhttpclient436:libs/httpclient-4.3.6.jar \
					libandroidasynchttp149:libs/android-async-http-1.4.9.jar \
					alipaySdk-20160825:libs/alipaySdk-20160825.jar \
					wechat-sdk-android-with-mta-1.0.2:libs/wechat-sdk-android-with-mta-1.0.2.jar

include $(BUILD_MULTI_PREBUILT)

# Use the following include to make our test apk.
#include $(call all-makefiles-under,$(LOCAL_PATH))