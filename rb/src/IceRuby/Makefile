# **********************************************************************
#
# Copyright (c) 2003-2013 ZeroC, Inc. All rights reserved.
#
# This copy of Ice is licensed to you under the terms described in the
# ICE_LICENSE file included in this distribution.
#
# **********************************************************************

top_srcdir	= ../..

LIBFILENAME     = $(call mkrblibfilename,IceRuby,$(VERSION))
SONAME          = $(call mkrbsoname,IceRuby,$(SOVERSION))  
LIBNAME		= $(call mkrblibname,IceRuby)
TARGETS		= $(call mklibtargets,$(libdir)/$(LIBFILENAME),$(libdir)/$(SONAME),$(libdir)/$(LIBNAME))

OBJS		= Connection.o \
		  Communicator.o \
		  Endpoint.o \
		  Init.o \
		  ImplicitContext.o \
		  Logger.o \
		  ObjectFactory.o \
		  Operation.o \
		  Properties.o \
		  Proxy.o \
		  Slice.o \
		  Types.o \
		  Util.o

SRCS		= $(OBJS:.o=.cpp)

include $(top_srcdir)/config/Make.rules

CPPFLAGS	:= -I. -I.. $(CPPFLAGS) $(ICE_FLAGS) $(RUBY_FLAGS)
LINKWITH        := -L"$(libdir)" $(ICE_LIBS) $(RUBY_LIBS) $(CXXLIBS)

$(libdir)/$(LIBFILENAME): $(OBJS)
	rm -f $@
	$(call mkshlib,$@,$(SONAME),$(OBJS),$(LINKWITH))

$(libdir)/$(SONAME): $(libdir)/$(LIBFILENAME)
	rm -f $@
	ln -s $(LIBFILENAME) $@

$(libdir)/$(LIBNAME): $(libdir)/$(SONAME)
	rm -f $@
	ln -s $(SONAME) $@

install:: all
	$(call installlib,$(install_libdir),$(libdir),$(LIBFILENAME),$(SONAME),$(LIBNAME))

#include .depend
