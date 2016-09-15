LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := myJNI
LOCAL_SRC_FILES := sum.c

LOCAL_LDLIBS += -llog
LOCAL_LDLIBS += -lOpenSLES
LOCAL_LDLIBS += -landroid
include $(BUILD_SHARED_LIBRARY)