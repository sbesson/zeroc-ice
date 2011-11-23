// **********************************************************************
//
// Copyright (c) 2003
// ZeroC, Inc.
// Billerica, MA, USA
//
// All Rights Reserved.
//
// Ice is free software; you can redistribute it and/or modify it under
// the terms of the GNU General Public License version 2 as published by
// the Free Software Foundation.
//
// **********************************************************************

#ifndef ICE_OBJECT_H
#define ICE_OBJECT_H

#include <IceUtil/GCShared.h>
#include <IceUtil/Mutex.h>
#include <Ice/ObjectF.h>
#include <Ice/ProxyF.h>
#include <Ice/IncomingAsyncF.h>
#include <Ice/Current.h>

namespace IceInternal
{

class Incoming;
class BasicStream;

enum DispatchStatus
{
    DispatchOK,
    DispatchUserException,
    DispatchObjectNotExist,
    DispatchFacetNotExist,
    DispatchOperationNotExist,
    DispatchUnknownLocalException,
    DispatchUnknownUserException,
    DispatchUnknownException,
    DispatchAsync // "Pseudo dispatch status", used internally only to indicate async dispatch.
};

}

namespace Ice
{

class ICE_API Object : virtual public ::IceUtil::GCShared
{
public:

    Object();

    virtual bool operator==(const Object&) const;
    virtual bool operator!=(const Object&) const;
    virtual bool operator<(const Object&) const;
    virtual ::Ice::Int ice_hash() const;

    static const ::std::string __ids[];

    virtual bool ice_isA(const ::std::string&, const Current& = Current()) const;
    ::IceInternal::DispatchStatus ___ice_isA(::IceInternal::Incoming&, const Current&);

    virtual void ice_ping(const Current&  = Current()) const;
    ::IceInternal::DispatchStatus ___ice_ping(::IceInternal::Incoming&, const Current&);

    virtual ::std::vector< ::std::string> ice_ids(const Current& = Current()) const;
    ::IceInternal::DispatchStatus ___ice_ids(::IceInternal::Incoming&, const Current&);

    virtual const ::std::string& ice_id(const Current& = Current()) const;
    ::IceInternal::DispatchStatus ___ice_id(::IceInternal::Incoming&, const Current&);

    ::std::vector< ::std::string> ice_facets(const Current& = Current()) const;
    ::IceInternal::DispatchStatus ___ice_facets(::IceInternal::Incoming&, const Current&);

    static const ::std::string& ice_staticId();

    void __copyMembers(::Ice::ObjectPtr) const;
    virtual ::Ice::ObjectPtr ice_clone() const;

    virtual void ice_preMarshal();
    virtual void ice_postUnmarshal();

    static ::std::string __all[];
    virtual ::IceInternal::DispatchStatus __dispatch(::IceInternal::Incoming&, const Current&);

    virtual void __write(::IceInternal::BasicStream*, bool) const;
    virtual void __read(::IceInternal::BasicStream*, bool = true);

    virtual void __gcReachable(::IceUtil::GCObjectMultiSet&) const;
    virtual void __gcClear();

    void ice_addFacet(const ObjectPtr&, const ::std::string&);
    ObjectPtr ice_removeFacet(const ::std::string&);
    ObjectPtr ice_updateFacet(const ObjectPtr&, const ::std::string&);
    void ice_removeAllFacets();
    ObjectPtr ice_findFacet(const ::std::string&);
    ObjectPtr ice_findFacetPath(const ::std::vector< ::std::string>&, int);

private:

    std::map<std::string, ObjectPtr> _activeFacetMap;
    std::map<std::string, ObjectPtr>::iterator _activeFacetMapHint;
    ::IceUtil::Mutex _activeFacetMapMutex;
    static const char * const _kindOfObject;
};

class ICE_API Blobject : virtual public Object
{
public:

    // Returns true if ok, false if user exception.
    virtual bool ice_invoke(const std::vector<Byte>&, std::vector<Byte>&, const Current&) = 0;
    virtual ::IceInternal::DispatchStatus __dispatch(::IceInternal::Incoming&, const Current&);
};

class ICE_API BlobjectAsync : virtual public Object
{
public:

    // Returns true if ok, false if user exception.
    virtual void ice_invoke_async(const AMD_Object_ice_invokePtr&, const std::vector<Byte>&, const Current&) = 0;
    virtual ::IceInternal::DispatchStatus __dispatch(::IceInternal::Incoming&, const Current&);
};

}

#endif
