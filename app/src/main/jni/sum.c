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


typedef struct player_{
    SLObjectItf bqPlayerObject;
    SLPlayItf bqPlayerPlay;
    SLAndroidSimpleBufferQueueItf bqPlayerBufferQueue;
    SLPlaybackRateItf playbackRateItf;
    SLDynamicInterfaceManagementItf dynamicInterface;
    const void* buffer;
    int bufferSize;
}player_Body;

static player_Body* player[6];

int is_playing, is_done_buffer = 0;

//define our callback

void SLAPIENTRY play_callback( SLPlayItf player, void *context, SLuint32 event ){

    if( event & SL_PLAYEVENT_HEADATEND )
    is_done_buffer = 1;
}


JNIEXPORT void JNICALL Java_com_limwoon_musicwriter_NativeClass_createEngine
  (JNIEnv *env, jclass cls){

  slCreateEngine(&engineObject, 0, NULL, 0, NULL, NULL);
  (*engineObject)->Realize(engineObject, SL_BOOLEAN_FALSE);
  (*engineObject)->GetInterface(engineObject, SL_IID_ENGINE, &engineEngine);

  const SLInterfaceID ids[1] = { SL_IID_PLAYBACKRATE };
  const SLboolean req[1] = { SL_BOOLEAN_FALSE };

  (*engineEngine)->CreateOutputMix(engineEngine, &outputMixObject, 1, ids, req);

  (*outputMixObject)->Realize(outputMixObject, SL_BOOLEAN_FALSE);

  if( (*outputMixObject)->GetInterface( outputMixObject,
      SL_IID_VOLUME, &outputMixVolume ) != SL_RESULT_SUCCESS )
      outputMixVolume = NULL;
  }

JNIEXPORT void JNICALL Java_com_limwoon_musicwriter_NativeClass_createBufferFromAsset
(JNIEnv *env, jclass cls, jobject assetManager, jstring jfileDir){

AAssetManager* mgr = AAssetManager_fromJava(env, assetManager);
const char* dir = (*env)->GetStringUTFChars(env, jfileDir, NULL);

assetDir = AAssetManager_openDir(mgr, dir);
(*env)->ReleaseStringUTFChars(env, jfileDir, dir);

int i;
for(i=0; i<6; i++){
AAsset* asset = AAssetManager_open(mgr, AAssetDir_getNextFileName(assetDir), AASSET_MODE_UNKNOWN);
player[i]->buffer = AAsset_getBuffer(asset);
player[i]->bufferSize = AAsset_getLength(asset);
}
}

JNIEXPORT void JNICALL Java_com_limwoon_musicwriter_NativeClass_createBefferQueueAudioPlayer
(JNIEnv *env, jclass cls){

SLDataLocator_AndroidSimpleBufferQueue loc_bufq = {SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE, 2};
SLDataFormat_PCM format_pcm = {SL_DATAFORMAT_PCM, 1, SL_SAMPLINGRATE_8,
                               SL_PCMSAMPLEFORMAT_FIXED_16, SL_PCMSAMPLEFORMAT_FIXED_16,
                               SL_SPEAKER_FRONT_CENTER, SL_BYTEORDER_LITTLEENDIAN};
SLDataSource audioSrc = {&loc_bufq, &format_pcm};

SLDataLocator_OutputMix loc_outmix = {SL_DATALOCATOR_OUTPUTMIX, outputMixObject};
SLDataSink audioSnk = {&loc_outmix, NULL};

const SLInterfaceID ids[2] = {SL_IID_BUFFERQUEUE, SL_IID_DYNAMICINTERFACEMANAGEMENT};
const SLboolean req[2] = {SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE};

int i;
for(i=0; i<6; i++){
player[i] = (player_Body*)malloc(sizeof(player_Body));
(*engineEngine)->CreateAudioPlayer(engineEngine, (&player[i]->bqPlayerObject), &audioSrc, &audioSnk, 2, ids, req);
(*player[i]->bqPlayerObject)->Realize(player[i]->bqPlayerObject, SL_BOOLEAN_FALSE);

(*player[i]->bqPlayerObject)->GetInterface(player[i]->bqPlayerObject, SL_IID_PLAY, &(player[i]->bqPlayerPlay));
(*player[i]->bqPlayerObject)->GetInterface(player[i]->bqPlayerObject, SL_IID_BUFFERQUEUE, &(player[i]->bqPlayerBufferQueue));
(*player[i]->bqPlayerObject)->GetInterface(player[i]->bqPlayerObject, SL_IID_DYNAMICINTERFACEMANAGEMENT, (&player[i]->dynamicInterface));
(*player[i]->dynamicInterface)->AddInterface(player[i]->dynamicInterface, SL_IID_PLAYBACKRATE, SL_BOOLEAN_FALSE);
(*player[i]->bqPlayerObject)->GetInterface(player[i]->bqPlayerObject, SL_IID_PLAYBACKRATE, (&player[i]->playbackRateItf));
(*player[i]->bqPlayerObject)->GetInterface(player[i]->bqPlayerObject, SL_IID_ANDROIDSIMPLEBUFFERQUEUE, (&player[i]->bqPlayerBufferQueue));

LOGI("%d done", i);
}
}



JNIEXPORT void JNICALL Java_com_limwoon_musicwriter_NativeClass_setPlayingBufferQueue
(JNIEnv *env, jclass cls, jint tone, jint pitch){
(*player[tone]->playbackRateItf)->SetRate(player[tone]->playbackRateItf, 2000);
(*player[tone]->bqPlayerBufferQueue)->Enqueue(
(player[tone]->bqPlayerBufferQueue),
(player[tone]->buffer),
(player[tone]->bufferSize));

(*player[tone]->bqPlayerPlay)->SetPlayState(player[tone]->bqPlayerPlay, SL_PLAYSTATE_PLAYING );

LOGI("%l", player[tone]->buffer);
LOGI("%d buffersize", player[tone]->bufferSize);

}

JNIEXPORT void JNICALL Java_com_limwoon_musicwriter_NativeClass_setStopBufferQueue
(JNIEnv *env, jclass cls){

(*player[2]->bqPlayerPlay)->SetPlayState((*player[2]->bqPlayerPlay), SL_PLAYSTATE_STOPPED );
(*player[2]->bqPlayerBufferQueue)->Clear((*player[2]->bqPlayerBufferQueue));
}



JNIEXPORT void JNICALL Java_com_limwoon_musicwriter_NativeClass_createAssetAudioPlayer
(JNIEnv *env, jclass cls, jobject assetManager, jstring jfileDir){

AAssetManager* mgr = AAssetManager_fromJava(env, assetManager);
const char* dir = (*env)->GetStringUTFChars(env, jfileDir, NULL);

assetDir = AAssetManager_openDir(mgr, dir);
(*env)->ReleaseStringUTFChars(env, jfileDir, dir);

SLpermille minRate[6];
SLpermille maxRate[6];
SLpermille stepSize[6];
SLpermille rate[6];
SLuint32 capa[6];
const void* buffer[6];
int bufferSize[6];

int i;
for(i=0; i<6; i++){
AAsset* asset = AAssetManager_open(mgr, AAssetDir_getNextFileName(assetDir), AASSET_MODE_UNKNOWN);
fd[i] = AAsset_openFileDescriptor(asset, &start[i], &length[i]);
buffer[i] = AAsset_getBuffer(asset);
bufferSize[i] = AAsset_getLength(asset);


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
(*fdPlayerObject[i])->GetInterface(fdPlayerObject[i], SL_IID_PLAY, &fdPlayerPlay[i]);
(*fdPlayerObject[i])->GetInterface(fdPlayerObject[i], SL_IID_DYNAMICINTERFACEMANAGEMENT, (void*)&dynamicInterface[i]);
(*dynamicInterface[i])->AddInterface(dynamicInterface[i], SL_IID_PLAYBACKRATE, SL_BOOLEAN_FALSE);
(*fdPlayerObject[i])->GetInterface(fdPlayerObject[i], SL_IID_PLAYBACKRATE, &playbackRateItf[i]);
(*playbackRateItf[i])->GetRateRange(playbackRateItf[i], 0, (void*)&minRate[i], (void*)&maxRate[i], (void*)&stepSize[i], (void*)&capa[i]);

// (*fdPlayerPlay[i])->SetPlayState(fdPlayerPlay[i], SL_PLAYSTATE_PLAYING);
// (*fdPlayerPlay[i])->SetPlayState(fdPlayerPlay[i], SL_PLAYSTATE_STOPPED);
}
AAssetDir_close(assetDir);
}


JNIEXPORT void JNICALL Java_com_limwoon_musicwriter_NativeClass_setPlayingAssetAudioPlayer
(JNIEnv *env, jclass cls, jint tone, jint pitch){

(*playbackRateItf[tone])->SetRate(playbackRateItf[tone], 1000*pow(2, pitch/12.0));
(*fdPlayerPlay[tone])->SetPlayState(fdPlayerPlay[tone], SL_PLAYSTATE_PLAYING);
LOGI("%d  565", pitch);
}


JNIEXPORT void JNICALL Java_com_limwoon_musicwriter_NativeClass_setStopAssetAudioPlayer
(JNIEnv *env, jclass vls, jint tone){
int i;
for(i=0; i<6; i++)
(*fdPlayerPlay[i])->SetPlayState(fdPlayerPlay[i], SL_PLAYSTATE_STOPPED);
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