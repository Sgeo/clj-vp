#ifndef __VP_SDK
#define __VP_SDK

#if defined(WIN32) || defined(UNDER_CE)
#ifdef VPSDK_EXPORTS
	#ifdef VPSDK_STATIC
	#define VPSDK_API extern "C"
	#else
	#define VPSDK_API extern "C" __declspec(dllexport)
	#endif
#else
	#ifdef __cplusplus
		#ifdef VPSDK_STATIC
			#define VPSDK_API extern "C"
		#else
			#define VPSDK_API extern "C" __declspec(dllimport)
		#endif
	#else
		#ifdef VPSDK_STATIC
			#define VPSDK_API extern
		#else
			#define VPSDK_API __declspec(dllimport)
		#endif
	#endif
#endif
#else
	#ifdef __cplusplus
		#define VPSDK_API extern "C"
	#else
		#define VPSDK_API extern
	#endif
#endif

//API Version
#define VPSDK_VERSION 1

enum VPEvent
{
	VP_EVENT_CHAT,
	VP_EVENT_AVATAR_ADD,
	VP_EVENT_AVATAR_CHANGE,
	VP_EVENT_AVATAR_DELETE,
	VP_EVENT_OBJECT,
	VP_EVENT_OBJECT_CHANGE,
	VP_EVENT_OBJECT_DELETE,
	VP_EVENT_OBJECT_CLICK,
	VP_EVENT_WORLD_LIST,
	VP_EVENT_WORLD_SETTING,
	VP_EVENT_WORLD_SETTINGS_CHANGED,
	VP_EVENT_FRIEND,
	VP_EVENT_WORLD_DISCONNECT,
	VP_EVENT_UNIVERSE_DISCONNECT,
	VP_EVENT_USER_ATTRIBUTES,
	
	VP_HIGHEST_EVENT
};

enum VPCallback
{
	VP_CALLBACK_OBJECT_ADD,
	VP_CALLBACK_OBJECT_CHANGE,
	VP_CALLBACK_OBJECT_DELETE,
	VP_CALLBACK_GET_FRIENDS,
	VP_CALLBACK_FRIEND_ADD,
	VP_CALLBACK_FRIEND_DELETE,
	VP_HIGHEST_CALLBACK
};

//Ints
enum VPIntegerProperty
{
	VP_AVATAR_SESSION,
	VP_AVATAR_TYPE,
	VP_MY_TYPE,
	
	VP_OBJECT_ID,
	VP_OBJECT_TYPE,
	VP_OBJECT_TIME,
	VP_OBJECT_USER_ID,
	
	VP_WORLD_STATE,
	VP_WORLD_USERS,
	
	VP_REFERENCE_NUMBER,
	VP_CALLBACK,
	
	VP_USER_ID,
	VP_USER_REGISTRATION_TIME,
	VP_USER_ONLINE_TIME,
	VP_USER_LAST_LOGIN,
	
	VP_FRIEND_ID,
	VP_FRIEND_USER_ID,
	VP_FRIEND_ONLINE,
	
	VP_MY_USER_ID,
	VP_PROXY_TYPE,
	VP_PROXY_PORT,
	
	VP_HIGHEST_INT
};

//Floats
enum VPFloatProperty
{
	VP_AVATAR_X,
	VP_AVATAR_Y,
	VP_AVATAR_Z,
	VP_AVATAR_YAW,
	VP_AVATAR_PITCH,
	
	VP_MY_X,
	VP_MY_Y,
	VP_MY_Z,
	VP_MY_YAW,
	VP_MY_PITCH,
	
	VP_OBJECT_X,
	VP_OBJECT_Y,
	VP_OBJECT_Z,
	VP_OBJECT_ROTATION_X,
	VP_OBJECT_ROTATION_Y,
	VP_OBJECT_ROTATION_Z,
	VP_OBJECT_YAW = VP_OBJECT_ROTATION_X,
	VP_OBJECT_PITCH = VP_OBJECT_ROTATION_Y,
	VP_OBJECT_ROLL = VP_OBJECT_ROTATION_Z,
	VP_OBJECT_ROTATION_ANGLE,
	
	VP_HIGHEST_FLOAT
};

//Strings
enum VPStringProperty
{
	VP_AVATAR_NAME,
	VP_CHAT_MESSAGE,
	
	VP_OBJECT_MODEL,
	VP_OBJECT_ACTION,
	VP_OBJECT_DESCRIPTION,
	
	VP_WORLD_NAME,
	
	VP_USER_NAME,
	VP_USER_EMAIL,
	
	VP_WORLD_SETTING_KEY,
	VP_WORLD_SETTING_VALUE,
	
	VP_FRIEND_NAME,
	VP_PROXY_HOST,
	
	VP_HIGHEST_STRING
};

//Data
enum VPDataProperty
{
	VP_OBJECT_DATA,
	VP_HIGHEST_DATA
};

//Proxy types
enum VPProxyType {
	VP_PROXY_TYPE_NONE,
	VP_PROXY_TYPE_SOCKS4A
};

typedef struct VPInstance_ *VPInstance;

/**
 *  Initialize the Virtual Paradise SDK API
 */
VPSDK_API int vp_init(int version);

/**
 *  Create a new instance.
 *  \return New instance or NULL on failure.
 */
VPSDK_API VPInstance vp_create();

/**
 *  Destroy a Virtual Paradise SDK instance.
 */
VPSDK_API int vp_destroy(VPInstance instance);

/**
 *  Connect to a universe server
 *  \param instance
 *  \param host Host address of server to connect to.
 *  \param port TCP port of remote server.
 *  \return Zero when successful, otherwise nonzero. See RC.h
 */
VPSDK_API int vp_connect_universe(VPInstance instance, const char * host, int port);

/**
 *  Login to the universe server
 *  \param instance
 *  \param username
 *  \param password
 *  \param botname
 *  \return Zero when successful, otherwise nonzero. See RC.h
 */
VPSDK_API int vp_login(VPInstance instance, const char * username, const char * password, const char * botname);

/**
 *  Wait for incoming messages.
 *  \param milliseconds The maximum time to wait in milliseconds.
 *  \return Zero when successful, otherwise nonzero. See RC.h
 */
VPSDK_API int vp_wait(VPInstance instance, int milliseconds);

/**
 *  Enter a world. The current world will be left.
 *  \return Zero when successful, otherwise nonzero. See RC.h
 */
VPSDK_API int vp_enter(VPInstance instance, const char * worldname);

/**
 *  Send a message to everyone in the current world.
 *  \param message The message to send.
 *  \return Zero when successful, otherwise nonzero. See RC.h
 */
VPSDK_API int vp_say(VPInstance instance, const char * message);

/**
 *  Register an event handler.
 *  \return Zero when successful, otherwise nonzero. See RC.h
 */
VPSDK_API int vp_event_set(VPInstance instance, VPEvent eventname, void (*event)(VPInstance sender));

/**
 *  Register a callback function.
 *  \return Zero when successful, otherwise nonzero. See RC.h
 */
VPSDK_API int vp_callback_set(VPInstance instance, VPCallback callbackname, void (*callback)(VPInstance sender, int rc, int reference));

/**
 *  Retrieve the pointer to user-defined data for this instance.
 *  \return Pointer to user-defined data.
 */
VPSDK_API void * vp_user_data(VPInstance instance);

/**
 *  Sets a pointer to user-defined data for this instance.
 *  This pointer is not accessed in any way, allocating and freeing it is the responsibility of the application programmer.
 *  \param data The pointer to your user-defined data.
 */
VPSDK_API void vp_user_data_set(VPInstance instance, void * data);
VPSDK_API int vp_state_change(VPInstance instance);

VPSDK_API int vp_int(VPInstance instance, VPIntegerProperty name);
VPSDK_API float vp_float(VPInstance instance, VPFloatProperty name);
VPSDK_API char* vp_string(VPInstance instance, VPStringProperty name);
VPSDK_API char* vp_data(VPInstance instance, VPDataProperty name, int* length);

VPSDK_API int vp_int_get(VPInstance instance, VPIntegerProperty name, int* value);
VPSDK_API int vp_float_get(VPInstance instance, VPFloatProperty name, float* value);
VPSDK_API int vp_string_get(VPInstance instance, VPStringProperty name, char** value);

VPSDK_API int vp_int_set(VPInstance instance, VPIntegerProperty name, int value);
VPSDK_API int vp_float_set(VPInstance instance, VPFloatProperty name, float value);
VPSDK_API void vp_string_set(VPInstance instance, VPStringProperty name, const char * str);
VPSDK_API int vp_data_set(VPInstance instance, VPDataProperty name, int length, char* data);

VPSDK_API int vp_query_cell(VPInstance instance, int x, int z);

VPSDK_API int vp_object_add(VPInstance instance);
VPSDK_API int vp_object_change(VPInstance instance);
VPSDK_API int vp_object_delete(VPInstance instance);
VPSDK_API int vp_object_click(VPInstance instance);

/**
 *  Request the world list.
 *  The worlds will be listed in the #VP_EVENT_WORLD_LIST event. See vp_event_set().
 *  \param time Time since your last update. This is not used yet, the whole list will be requested.
 */
VPSDK_API int vp_world_list(VPInstance instance, int time);

//VPSDK_API void* vp_callback_pointer(VPInstance instance);
//VPSDK_API void vp_callback_pointer_set(VPInstance instance, void* ptr);

/**
 *  Request user attributes by user ID.
 *  The user attributes will be returned in the #VP_EVENT_USER_ATTRIBUTES event.
 *  \return Zero when successful, otherwise nonzero. This value will return the result
 */
VPSDK_API int vp_user_attributes_by_id(VPInstance instance, int user_id);

/**
 *  Get user attributes by user name. Not implemented.
 */
VPSDK_API int vp_user_attributes_by_name(VPInstance instance, const char * name);

VPSDK_API int vp_friends_get(VPInstance instance);
VPSDK_API int vp_friend_add_by_name(VPInstance instance, const char* name);
VPSDK_API int vp_friend_delete(VPInstance instance, int friend_id);

#endif
