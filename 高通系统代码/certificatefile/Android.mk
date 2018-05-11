LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

#################################################
$(shell mkdir -p $(TARGET_OUT)/wifi/sdcard)
$(shell cp -r $(LOCAL_PATH)/*.cer $(TARGET_OUT)/wifi/sdcard)
