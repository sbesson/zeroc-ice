# **********************************************************************
#
# Copyright (c) 2003-2013 ZeroC, Inc. All rights reserved.
#
# This copy of Ice is licensed to you under the terms described in the
# ICE_LICENSE file included in this distribution.
#
# **********************************************************************

top_srcdir	= ../..

NAME		= $(bindir)/slice2cpp$(EXE_EXT)

TARGETS		= $(NAME)

OBJS		= Gen.o \
		  Main.o

SRCS		= $(OBJS:.o=.cpp)

ifeq ($(CPP11),yes)
RPATH_DIR	= @loader_path/../../lib/c++11
else
RPATH_DIR	= @loader_path/../lib
endif

include $(top_srcdir)/config/Make.rules

CPPFLAGS	:= -I. $(CPPFLAGS)

$(NAME): $(OBJS)
	rm -f $@
	$(CXX) $(LDFLAGS) -o $@ $(OBJS) $(SLICE_LIBS) $(MCPP_RPATH_LINK)

install:: all
	$(call installprogram,$(NAME),$(install_bindir))

include .depend
