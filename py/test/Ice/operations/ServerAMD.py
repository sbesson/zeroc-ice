#!/usr/bin/env python
# **********************************************************************
#
# Copyright (c) 2003-2009 ZeroC, Inc. All rights reserved.
#
# This copy of Ice is licensed to you under the terms described in the
# ICE_LICENSE file included in this distribution.
#
# **********************************************************************

import os, sys, traceback, threading

import Ice
slice_dir = Ice.getSliceDir()
if not slice_dir:
    print sys.argv[0] + ': Slice directory not found.'
    sys.exit(1)

Ice.loadSlice('-I' + slice_dir + ' TestAMD.ice')
import Test

class Thread_opVoid(threading.Thread):
    def __init__(self, cb):
        threading.Thread.__init__(self)
        self.cb = cb

    def run(self):
        self.cb.ice_response()

class MyDerivedClassI(Test.MyDerivedClass):
    def __init__(self):
        self.opVoidThread = None
        self.opVoidThreadLock = threading.Lock()

    def shutdown_async(self, cb, current=None):
        self.opVoidThreadLock.acquire()
        if self.opVoidThread:
            self.opVoidThread.join()
            self.opVoidThread = None
        self.opVoidThreadLock.release()

        current.adapter.getCommunicator().shutdown()
        cb.ice_response()

    def opVoid_async(self, cb, current=None):
        self.opVoidThreadLock.acquire()
        if self.opVoidThread:
            self.opVoidThread.join()
            self.opVoidThread = None

        self.opVoidThread = Thread_opVoid(cb)
        self.opVoidThread.start()
        self.opVoidThreadLock.release()

    def opByte_async(self, cb, p1, p2, current=None):
        cb.ice_response(p1, p1 ^ p2)

    def opBool_async(self, cb, p1, p2, current=None):
        cb.ice_response(p2, p1)

    def opShortIntLong_async(self, cb, p1, p2, p3, current=None):
        cb.ice_response(p3, p1, p2, p3)

    def opFloatDouble_async(self, cb, p1, p2, current=None):
        cb.ice_response(p2, p1, p2)

    def opString_async(self, cb, p1, p2, current=None):
        cb.ice_response(p1 + " " + p2, p2 + " " + p1)

    def opMyEnum_async(self, cb, p1, current=None):
        cb.ice_response(Test.MyEnum.enum3, p1)

    def opMyClass_async(self, cb, p1, current=None):
        p2 = p1
        p3 = Test.MyClassPrx.uncheckedCast(current.adapter.createProxy(communicator.stringToIdentity("noSuchIdentity")))
        cb.ice_response(Test.MyClassPrx.uncheckedCast(current.adapter.createProxy(current.id)), p2, p3)

    def opStruct_async(self, cb, p1, p2, current=None):
        p1.s.s = "a new string"
        cb.ice_response(p2, p1)

    def opByteS_async(self, cb, p1, p2, current=None):
        # By default sequence<byte> maps to a string.
        p3 = map(ord, p1)
        p3.reverse()
        r = map(ord, p1)
        r.extend(map(ord, p2))
        cb.ice_response(r, p3)

    def opBoolS_async(self, cb, p1, p2, current=None):
        p3 = p1[0:]
        p3.extend(p2)
        r = p1[0:]
        r.reverse();
        cb.ice_response(r, p3)

    def opShortIntLongS_async(self, cb, p1, p2, p3, current=None):
        p4 = p1[0:]
        p5 = p2[0:]
        p5.reverse()
        p6 = p3[0:]
        p6.extend(p3)
        cb.ice_response(p3, p4, p5, p6)

    def opFloatDoubleS_async(self, cb, p1, p2, current=None):
        p3 = p1[0:]
        p4 = p2[0:]
        p4.reverse()
        r = p2[0:]
        r.extend(p1)
        cb.ice_response(r, p3, p4)

    def opStringS_async(self, cb, p1, p2, current=None):
        p3 = p1[0:]
        p3.extend(p2)
        r = p1[0:]
        r.reverse()
        cb.ice_response(r, p3)

    def opByteSS_async(self, cb, p1, p2, current=None):
        p3 = p1[0:]
        p3.reverse()
        r = p1[0:]
        r.extend(p2)
        cb.ice_response(r, p3)

    def opBoolSS_async(self, cb, p1, p2, current=None):
        p3 = p1[0:]
        p3.extend(p2)
        r = p1[0:]
        r.reverse()
        cb.ice_response(r, p3)

    def opShortIntLongSS_async(self, cb, p1, p2, p3, current=None):
        p4 = p1[0:]
        p5 = p2[0:]
        p5.reverse()
        p6 = p3[0:]
        p6.extend(p3)
        cb.ice_response(p3, p4, p5, p6)

    def opFloatDoubleSS_async(self, cb, p1, p2, current=None):
        p3 = p1[0:]
        p4 = p2[0:]
        p4.reverse()
        r = p2[0:]
        r.extend(p2)
        cb.ice_response(r, p3, p4)

    def opStringSS_async(self, cb, p1, p2, current=None):
        p3 = p1[0:]
        p3.extend(p2)
        r = p2[0:]
        r.reverse()
        cb.ice_response(r, p3)

    def opStringSSS_async(self, cb, p1, p2, current=None):
        p3 = p1[0:]
        p3.extend(p2)
        r = p2[0:]
        r.reverse()
        cb.ice_response(r, p3)

    def opByteBoolD_async(self, cb, p1, p2, current=None):
        p3 = p1.copy()
        r = p1.copy()
        r.update(p2)
        cb.ice_response(r, p3)

    def opShortIntD_async(self, cb, p1, p2, current=None):
        p3 = p1.copy()
        r = p1.copy()
        r.update(p2)
        cb.ice_response(r, p3)

    def opLongFloatD_async(self, cb, p1, p2, current=None):
        p3 = p1.copy()
        r = p1.copy()
        r.update(p2)
        cb.ice_response(r, p3)

    def opStringStringD_async(self, cb, p1, p2, current=None):
        p3 = p1.copy()
        r = p1.copy()
        r.update(p2)
        cb.ice_response(r, p3)

    def opStringMyEnumD_async(self, cb, p1, p2, current=None):
        p3 = p1.copy()
        r = p1.copy()
        r.update(p2)
        cb.ice_response(r, p3)

    def opMyStructMyEnumD_async(self, cb, p1, p2, current=None):
        p3 = p1.copy()
        r = p1.copy()
        r.update(p2)
        cb.ice_response(r, p3)

    def opIntS_async(self, cb, s, current=None):
        cb.ice_response([-x for x in s])

    def opByteSOneway_async(self, cb, s, current=None):
        cb.ice_response()

    def opContext_async(self, cb, current=None):
        cb.ice_response(current.ctx)

    def opDerived_async(self, cb, current=None):
        cb.ice_response()

def run(args, communicator):
    communicator.getProperties().setProperty("TestAdapter.Endpoints", "default -p 12010 -t 10000:udp")
    adapter = communicator.createObjectAdapter("TestAdapter")
    adapter.add(MyDerivedClassI(), communicator.stringToIdentity("test"))
    adapter.activate()
    communicator.waitForShutdown()
    return True

try:
    initData = Ice.InitializationData()
    initData.properties = Ice.createProperties(sys.argv)
    initData.properties.setProperty("Ice.Warn.Dispatch", "0");
    communicator = Ice.initialize(sys.argv, initData)
    status = run(sys.argv, communicator)
except:
    traceback.print_exc()
    status = False

if communicator:
    try:
        communicator.destroy()
    except:
        traceback.print_exc()
        status = False

sys.exit(not status)