#ifndef VPSDK_RC_H
#define VPSDK_RC_H

enum VPReturnCode
{
	VP_RC_SUCCESS,
	VP_RC_VERSION_MISMATCH,
	VP_RC_NOT_INITIALIZED,
	VP_RC_ALREADY_INITIALIZED,
	VP_RC_STRING_TOO_LONG,
	VP_RC_INVALID_LOGIN,
	VP_RC_WORLD_NOT_FOUND,
	VP_RC_WORLD_LOGIN_ERROR,
	VP_RC_NOT_IN_WORLD,
	VP_RC_CONNECTION_ERROR,
	VP_RC_NO_INSTANCE,
	VP_RC_NOT_IMPLEMENTED,
	VP_RC_NO_SUCH_ATTRIBUTE,
	VP_RC_NOT_ALLOWED,
	VP_RC_DATABASE_ERROR,
	VP_RC_NO_SUCH_USER,
	VP_RC_TIMEOUT,
};

#endif

