# **********************************************************************
#
# Copyright (c) 2003-2013 ZeroC, Inc. All rights reserved.
#
# This copy of Ice is licensed to you under the terms described in the
# ICE_LICENSE file included in this distribution.
#
# **********************************************************************

top_srcdir	= ../../..

CLIENT		= client$(EXE_EXT)
SERVER		= server$(EXE_EXT)
SERVERCUSTOM	= servercustom$(EXE_EXT)

TARGETS		= $(CLIENT) $(SERVER) $(SERVERCUSTOM)

COBJS		= Test.o \
		  Client.o \
		  AllTests.o

SOBJS		= Test.o \
		  TestI.o \
		  Server.o

SCOBJS		= ServerCustomThreadPool.o \
		  Test.o \
		  TestI.o

SRCS		= $(COBJS:.o=.cpp) \
		  $(SOBJS:.o=.cpp) \
		  $(SCOBJS:.o=.cpp)

SLICE_SRCS	= Test.ice

include $(top_srcdir)/config/Make.rules

CPPFLAGS	:= -I. -I../../include $(CPPFLAGS)

$(CLIENT): $(COBJS)
	rm -f $@
	$(CXX) $(LDFLAGS) -o $@ $(COBJS) $(LIBS)

$(SERVER): $(SOBJS)
	rm -f $@
	$(CXX) $(LDFLAGS) -o $@ $(SOBJS) $(LIBS)

$(SERVERCUSTOM): $(SCOBJS)
	rm -f $@
	$(CXX) $(LDFLAGS) -o $@ $(SCOBJS) $(LIBS)

include .depend
