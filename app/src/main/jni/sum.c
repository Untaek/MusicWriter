//
// Created by ejdej on 2016-08-24.
//


#include "com_limwoon_musicwriter_NativeClass.h"

#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>
#include <SLES/OpenSLES_AndroidConfiguration.h>

#include <sys/types.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>

#include <stdio.h>
#include <assert.h>
#include <android/log.h>
#include <math.h>

#define TRUE 1
#define FALSE 0
#define LOGI(...) __android_log_print(ANDROID_LOG_DEBUG, "libbbb", __VA_ARGS__)

static SLObjectItf engineObject = NULL;
static SLEngineItf engineEngine;

static SLObjectItf outputMixObject = NULL;
static SLVolumeItf outputMixVolume;
static SLEnvironmentalReverbItf outputMixEnvironmentalReverb = NULL;

static SLObjectItf fdPlayerObject[6];
static SLPlayItf fdPlayerPlay[6];
static SLSeekItf fdPlayerSeek;
static SLMuteSoloItf fdPlayerMuteSolo;
static SLVolumeItf fdPlayerVolume;
static SLPlaybackRateItf playbackRateItf[6];
static SLDynamicInterfaceManagementItf dynamicInterface[6];

static off_t start[6], length[6];

static int fd[6];

static AAssetDir* assetDir;

JNIEXPORT void JNICALL Java_com_limwoon_musicwriter_NativeClass_createEngine
  (JNIEnv *env, jclass cls){

  slCreateEngine(&engineObject, 0, NULL, 0, NULL, NULL);
  (*engineObject)->Realize(engineObject, SL_BOOLEAN_FALSE);
  (*engineObject)->GetInterface(engineObject, SL_IID_ENGINE, &engineEngine);

  const SLInterfaceID ids[] = { SL_IID_VOLUME };
  const SLboolean req[] = { SL_BOOLEAN_FALSE };

  (*engineEngine)->CreateOutputMix(engineEngine, &outputMixObject, 1, ids, req);

  (*outputMixObject)->Realize(outputMixObject, SL_BOOLEAN_FALSE);

  if( (*outputMixObject)->GetInterface( outputMixObject,
      SL_IID_VOLUME, &outputMixVolume ) != SL_RESULT_SUCCESS )
      outputMixVolume = NULL;
  }

JNIEXPORT void JNICALL Java_com_limwoon_musicwriter_NativeClass_createAssetAudioPlayer
  (JNIEnv *env, jclass cls, jobject assetManager, jstring jfileDir){

  AAssetManager* mgr = AAssetManager_fromJava(env, assetManager);
  const char* dir = (*env)->GetStringUTFChars(env, jfileDir, NULL);

  assetDir = AAssetManager_openDir(mgr, dir);
  (*env)->ReleaseStringUTFChars(env, jfileDir, dir);

    SLpermille minRate[6] = {0,};
    SLpermille maxRate[6] = {4000,};
    SLpermille stepSize[6] ={0,};
    SLpermille rate[6];
    SLuint32 capa[6];

    int i;
    for(i=0; i<6; i++){
        AAsset* asset = AAssetManager_open(mgr, AAssetDir_getNextFileName(assetDir), AASSET_MODE_UNKNOWN);
        fd[i] = AAsset_openFileDescriptor(asset, &start[i], &length[i]);

        SLDataLocator_AndroidFD loc_fd = {SL_DATALOCATOR_ANDROIDFD, fd[i], start[i], length[i]};
        SLDataFormat_MIME format_mime = {SL_DATAFORMAT_MIME, NULL, SL_CONTAINERTYPE_UNSPECIFIED};
        SLDataSource audioSrc = {&loc_fd, &format_mime};

        SLDataLocator_AndroidSimpleBufferQueue loc_bq = {SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE, 2};
        SLDataLocator_OutputMix loc_outmix = {SL_DATALOCATOR_OUTPUTMIX, outputMixObject};

        SLDataSink audioSnk = {&loc_outmix, NULL};

        // create audio player
        const SLInterfaceID ids[] = {SL_IID_PLAY, SL_IID_DYNAMICINTERFACEMANAGEMENT};
        const SLboolean req[] = {SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE};
        SLresult result;
        (*engineEngine)->CreateAudioPlayer(engineEngine, &fdPlayerObject[i], &audioSrc, &audioSnk, 2, ids, req);

        // realize the player
        (*fdPlayerObject[i])->Realize(fdPlayerObject[i], SL_BOOLEAN_FALSE);
        (*fdPlayerObject[i])->GetInterface(fdPlayerObject[i], SL_IID_DYNAMICINTERFACEMANAGEMENT, (void*)&dynamicInterface[i]);
        (*dynamicInterface[i])->AddInterface(dynamicInterface[i], SL_IID_PLAYBACKRATE, SL_BOOLEAN_FALSE);
        (*fdPlayerObject[i])->GetInterface(fdPlayerObject[i], SL_IID_PLAYBACKRATE, &playbackRateItf[i]);
        (*playbackRateItf[i])->GetRateRange(playbackRateItf[i], 0, (void*)&minRate[i], (void*)&maxRate[i], (void*)&stepSize[i], (void*)&capa[i]);
        (*fdPlayerObject[i])->GetInterface(fdPlayerObject[i], SL_IID_PLAY, &fdPlayerPlay[i]);
        (*fdPlayerPlay[i])->SetPlayState(fdPlayerPlay[i], SL_PLAYSTATE_PLAYING);
        (*fdPlayerPlay[i])->SetPlayState(fdPlayerPlay[i], SL_PLAYSTATE_STOPPED);
    }
  AAssetDir_close(assetDir);
}

JNIEXPORT void JNICALL Java_com_limwoon_musicwriter_NativeClass_setPlayingAssetAudioPlayer
  (JNIEnv *env, jclass cls, jint tone, jint pitch){

     (*playbackRateItf[tone])->SetRate(playbackRateItf[tone], 1000*pow(2, pitch/12.0));
     (*fdPlayerPlay[tone])->SetPlayState(fdPlayerPlay[tone], SL_PLAYSTATE_PLAYING);
}

JNIEXPORT void JNICALL Java_com_limwoon_musicwriter_NativeClass_setStopAssetAudioPlayer
  (JNIEnv *env, jclass vls, jint tone){
        int i;
        for(i=0; i<6; i++)
            (*fdPlayerPlay[i])->SetPlayState(fdPlayerPlay[i], SL_PLAYSTATE_STOPPED);
  }


JNIEXPORT void JNICALL Java_com_limwoon_musicwriter_NativeClass_setPitch
                          (JNIEnv *env, jclass cls, jobject assetManager, jobjectArray jfileList) {



}

JNIEXPORT jboolean JNICALL Java_com_limwoon_musicwriter_NativeClass_getArrayList
                                                (JNIEnv *env, jclass cls, jobject objArrayListAAA){
        // ArrayList<AAA> 형식의 클래스 가져오기
        jclass clsArrayListAAA = (*env)->FindClass(env, "java/util/ArrayList");

        // ArrayList의 멤버함수인 size, get을 사용하기 위해 methodID를 가져오기
        // 아래에서 얻어온 메소드ID로 ArrayList<AAA>와 ArrayList<BBB> 등, 모든 ArrayList<type> 형식의 size, get 함수 사용이 가능하다.
        jmethodID midSize = (*env)->GetMethodID(env, clsArrayListAAA, "size", "()I");
        jmethodID midGet = (*env)->GetMethodID(env, clsArrayListAAA, "get", "(I)Ljava/lang/Object;");

        // ArrayList<AAA>에서 AAA 클래스 가져오기
        jobject objAAA = (*env)->CallObjectMethod(env, objArrayListAAA, midGet, 0);
        jclass clsAAA = (*env)->GetObjectClass(env, objAAA);

        // AAA 클래스의 필드 가져오기
        // 필드의 값을 얻어오는 게 아니라, 필드 ID를 얻어와서 나중에 필드의 값을 얻어올 때 사용한다.

        jfieldID durationID = (*env)->GetFieldID(env, clsAAA, "duration", "I");
        jfieldID toneID = (*env)->GetFieldID(env, clsAAA, "tone", "[I");
        jfieldID restID = (*env)->GetFieldID(env, clsAAA, "rest", "Z");
        jfieldID nodeID = (*env)->GetFieldID(env, clsAAA, "node", "Z");

        int listSize = (*env)->CallIntMethod(env, objArrayListAAA, midSize);

        int duration[listSize];
        int tone[listSize][6];
        int rest[listSize];
        int node[listSize];

        int i,j;
        for(i=0; i<listSize; i++){
            jobject objData = (*env)->CallObjectMethod(env, objArrayListAAA, midGet, i);
            duration[i] = (*env)->GetIntField(env, objData, durationID);
            rest[i] = (*env)->GetBooleanField(env, objData, restID);
            node[i] = (*env)->GetBooleanField(env, objData, nodeID);
            jintArray jarray = (*env)->GetObjectField(env, objData, toneID);
            jint* array = (*env)->GetIntArrayElements(env, jarray, 0);
            for(j=0; j<6; j++){
                tone[i][j]=array[j];
            }
            (*env)->ReleaseIntArrayElements(env, jarray, array, 0);
        }
    return TRUE;
  }

  JNIEXPORT void JNICALL Java_com_limwoon_musicwriter_NativeClass_releaseAll
    (JNIEnv *env, jclass cls){

        int i;
        for(i=0; i<6; i++)
            if (fdPlayerObject[i] != NULL) {
                (*fdPlayerObject[i])->Destroy(fdPlayerObject[i]);
                fdPlayerObject[i] = NULL;
                fdPlayerPlay[i] = NULL;
                fdPlayerSeek = NULL;
                fdPlayerMuteSolo = NULL;
                fdPlayerVolume = NULL;
                playbackRateItf[i] = NULL;
                dynamicInterface[i] = NULL;
            }

        if(outputMixObject != NULL){
            (*outputMixObject)->Destroy(outputMixObject);
            outputMixObject=NULL;
            outputMixVolume=NULL;
        }
        if(engineObject != NULL){
            (*engineObject)->Destroy(engineObject);
            engineObject=NULL;
            engineEngine=NULL;
        }
    }