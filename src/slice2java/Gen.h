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

#ifndef GEN_H
#define GEN_H

#include <Slice/Parser.h>
#include <IceUtil/OutputUtil.h>
#include <Slice/JavaUtil.h>

namespace Slice
{

class JavaVisitor : public JavaGenerator, public ParserVisitor
{
public:

    virtual ~JavaVisitor();

protected:

    JavaVisitor(const std::string&);

    //
    // Compose the parameter lists for an operation.
    //
    std::vector<std::string> getParams(const OperationPtr&, const std::string&);
    std::vector<std::string> getParamsAsync(const OperationPtr&, const std::string&, bool);
    std::vector<std::string> getParamsAsyncCB(const OperationPtr&, const std::string&);

    //
    // Compose the argument lists for an operation.
    //
    std::vector<std::string> getArgs(const OperationPtr&);
    std::vector<std::string> getArgsAsync(const OperationPtr&);
    std::vector<std::string> getArgsAsyncCB(const OperationPtr&);

    //
    // Generate a throws clause containing only non-local exceptions.
    //
    void writeThrowsClause(const std::string&, const ExceptionList&);

    //
    // Generate a throws clause for delegate operations containing only
    // non-local exceptions.
    //
    void writeDelegateThrowsClause(const std::string&, const ExceptionList&);

    //
    // Generate code to compute a hash code for a type.
    //
    void writeHashCode(::IceUtil::Output&, const TypePtr&, const std::string&, int&,
                       const std::list<std::string>& = std::list<std::string>());

    //
    // Generate dispatch methods for a class or interface.
    //
    void writeDispatch(::IceUtil::Output&, const ClassDefPtr&);
};

class Gen : public ::IceUtil::noncopyable
{
public:

    Gen(const std::string&,
        const std::string&,
        const std::vector<std::string>&,
        const std::string&);
    ~Gen();

    bool operator!() const; // Returns true if there was a constructor error

    void generate(const UnitPtr&);
    void generateTie(const UnitPtr&);
    void generateImpl(const UnitPtr&);
    void generateImplTie(const UnitPtr&);

private:

    std::string _base;
    std::vector<std::string> _includePaths;
    std::string _dir;

    class OpsVisitor : public JavaVisitor
    {
    public:

        OpsVisitor(const std::string&);

        virtual bool visitClassDefStart(const ClassDefPtr&);
        virtual void visitClassDefEnd(const ClassDefPtr&);
        virtual void visitOperation(const OperationPtr&);
    };

    class TieVisitor : public JavaVisitor
    {
    public:

        TieVisitor(const std::string&);

        virtual bool visitClassDefStart(const ClassDefPtr&);
    };

    class TypesVisitor : public JavaVisitor
    {
    public:

        TypesVisitor(const std::string&);

        virtual bool visitClassDefStart(const ClassDefPtr&);
        virtual void visitClassDefEnd(const ClassDefPtr&);
        virtual bool visitExceptionStart(const ExceptionPtr&);
        virtual void visitExceptionEnd(const ExceptionPtr&);
        virtual bool visitStructStart(const StructPtr&);
        virtual void visitStructEnd(const StructPtr&);
        virtual void visitEnum(const EnumPtr&);
        virtual void visitConst(const ConstPtr&);
        virtual void visitDataMember(const DataMemberPtr&);
    };

    class HolderVisitor : public JavaVisitor
    {
    public:

        HolderVisitor(const std::string&);

        virtual bool visitClassDefStart(const ClassDefPtr&);
        virtual bool visitStructStart(const StructPtr&);
        virtual void visitSequence(const SequencePtr&);
        virtual void visitDictionary(const DictionaryPtr&);
        virtual void visitEnum(const EnumPtr&);

    private:

        void writeHolder(const TypePtr&);
    };

    class HelperVisitor : public JavaVisitor
    {
    public:

        HelperVisitor(const std::string&);

        virtual bool visitClassDefStart(const ClassDefPtr&);
        virtual void visitSequence(const SequencePtr&);
        virtual void visitDictionary(const DictionaryPtr&);
    };

    class ProxyVisitor : public JavaVisitor
    {
    public:

        ProxyVisitor(const std::string&);

        virtual bool visitClassDefStart(const ClassDefPtr&);
        virtual void visitClassDefEnd(const ClassDefPtr&);
        virtual void visitOperation(const OperationPtr&);
    };

    class DelegateVisitor : public JavaVisitor
    {
    public:

        DelegateVisitor(const std::string&);

        virtual bool visitClassDefStart(const ClassDefPtr&);
    };

    class DelegateMVisitor : public JavaVisitor
    {
    public:

        DelegateMVisitor(const std::string&);

        virtual bool visitClassDefStart(const ClassDefPtr&);
    };

    class DelegateDVisitor : public JavaVisitor
    {
    public:

        DelegateDVisitor(const std::string&);

        virtual bool visitClassDefStart(const ClassDefPtr&);
    };

    class DispatcherVisitor : public JavaVisitor
    {
    public:

        DispatcherVisitor(const std::string&);

        virtual bool visitClassDefStart(const ClassDefPtr&);
    };

    class BaseImplVisitor : public JavaVisitor
    {
    public:

        BaseImplVisitor(const std::string&);

    protected:

        //
        // Generate code to emit a local variable declaration and initialize it
        // if necessary.
        //
        void writeDecl(::IceUtil::Output&, const std::string&, const std::string&, const TypePtr&);

        //
        // Generate code to return a value.
        //
        void writeReturn(::IceUtil::Output&, const TypePtr&);

        //
        // Generate an operation.
        //
        void writeOperation(::IceUtil::Output&, const std::string&, const OperationPtr&, bool);
    };

    class ImplVisitor : public BaseImplVisitor
    {
    public:

        ImplVisitor(const std::string&);

        virtual bool visitClassDefStart(const ClassDefPtr&);
    };

    class ImplTieVisitor : public BaseImplVisitor
    {
    public:

        ImplTieVisitor(const std::string&);

        virtual bool visitClassDefStart(const ClassDefPtr&);
    };

    class AsyncVisitor : public JavaVisitor
    {
    public:

        AsyncVisitor(const std::string&);

        virtual void visitOperation(const OperationPtr&);
    };
};

}

#endif
