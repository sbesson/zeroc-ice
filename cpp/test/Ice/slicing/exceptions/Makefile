# **********************************************************************
#
# Copyright (c) 2003-2013 ZeroC, Inc. All rights reserved.
#
# This copy of Ice is licensed to you under the terms described in the
# ICE_LICENSE file included in this distribution.
#
# **********************************************************************

top_srcdir	= ../../../..

CLIENT		= client$(EXE_EXT)
SERVER		= server$(EXE_EXT)
SERVERAMD	= serveramd$(EXE_EXT)

TARGETS		= $(CLIENT) $(SERVER) $(SERVERAMD)

COBJS		= Test.o \
		  Client.o \
		  ClientPrivate.o \
		  AllTests.o

SOBJS		= Test.o \
    		  ServerPrivate.o \
		  TestI.o \
		  Server.o

SAMDOBJS	= TestAMD.o \
    		  ServerPrivateAMD.o \
		  TestAMDI.o \
		  ServerAMD.o

SRCS		= $(COBJS:.o=.cpp) \
		  $(SOBJS:.o=.cpp) \
		  $(SAMDOBJS:.o=.cpp)

SLICE_SRCS	= Test.ice TestAMD.ice ClientPrivate.ice ServerPrivate.ice ServerPrivateAMD.ice

include $(top_srcdir)/config/Make.rules

ICECPPFLAGS	:= -I. $(ICECPPFLAGS)

CPPFLAGS	:= -I. -I../../../include $(CPPFLAGS)

$(CLIENT): $(COBJS)
	rm -f $@
	$(CXX) $(LDFLAGS) -o $@ $(COBJS) $(LIBS)

$(SERVER): $(SOBJS)
	rm -f $@
	$(CXX) $(LDFLAGS) -o $@ $(SOBJS) $(LIBS)

$(SERVERAMD): $(SAMDOBJS)
	rm -f $@
	$(CXX) $(LDFLAGS) -o $@ $(SAMDOBJS) $(LIBS)

include .depend
