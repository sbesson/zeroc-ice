// **********************************************************************
//
// Copyright (c) 2003
// ZeroC, Inc.
// Huntsville, AL, USA
//
// All Rights Reserved.
//
// Ice is free software; you can redistribute it and/or modify it under
// the terms of the GNU General Public License version 2 as published by
// the Free Software Foundation.
//
// **********************************************************************

#include <Ice/Ice.h>
#include <HelloServiceI.h>
#include <HelloI.h>

using namespace std;

extern "C"
{

//
// Factory function
//
HELLO_API ::IceBox::Service*
create(::Ice::CommunicatorPtr communicator)
{
    return new HelloServiceI;
}

}

HelloServiceI::HelloServiceI()
{
}

HelloServiceI::~HelloServiceI()
{
}

void
HelloServiceI::start(const string& name,
		     const ::Ice::CommunicatorPtr& communicator,
		     const ::Ice::StringSeq& args)
{
    string id = communicator->getProperties()->getProperty("Identity");

    _adapter = communicator->createObjectAdapter(name);
    ::Ice::ObjectPtr object = new HelloI;
    _adapter->add(object, Ice::stringToIdentity(id));
    _adapter->activate();
}

void
HelloServiceI::stop()
{
    _adapter->deactivate();
}
