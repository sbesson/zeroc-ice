// **********************************************************************
//
// Copyright (c) 2003-2009 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

#ifndef HELLO_ICE
#define HELLO_ICE

module Demo
{

interface Hello
{
    ["ami", "cpp:const"] idempotent void sayHello(int delay);
    ["ami"] void shutdown();
};

};

#endif