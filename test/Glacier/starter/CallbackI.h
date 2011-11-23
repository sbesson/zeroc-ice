// **********************************************************************
//
// Copyright (c) 2003-2004 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

#ifndef CALLBACK_I_H
#define CALLBACK_I_H

#include <IceUtil/Mutex.h>
#include <IceUtil/Monitor.h>
#include <Callback.h>

class CallbackReceiverI : public CallbackReceiver, IceUtil::Monitor<IceUtil::Mutex>
{
public:

    CallbackReceiverI();

    virtual void callback(const Ice::Current&);
    virtual void callbackEx(const Ice::Current&);
    bool callbackOK();

private:

    bool _callback;
};

class CallbackI : public Callback
{
public:

    CallbackI(const Ice::CommunicatorPtr&);

    virtual void initiateCallback(const CallbackReceiverPrx&, const Ice::Current&);
    virtual void initiateCallbackEx(const CallbackReceiverPrx&, const Ice::Current&);
    virtual void shutdown(const Ice::Current&);

private:

    Ice::CommunicatorPtr _communicator;
};

#endif
