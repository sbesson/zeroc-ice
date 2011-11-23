// **********************************************************************
//
// Copyright (c) 2003-2009 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

package IceInternal;

public final class ReferenceFactory
{
    public Reference
    create(Ice.Identity ident, String facet, Reference tmpl, EndpointI[] endpoints)
    {
        if(ident.name.length() == 0 && ident.category.length() == 0)
        {
            return null;
        }

        return create(ident, facet, tmpl.getMode(), tmpl.getSecure(), endpoints, null, null);
    }

    public Reference
    create(Ice.Identity ident, String facet, Reference tmpl, String adapterId)
    {
        if(ident.name.length() == 0 && ident.category.length() == 0)
        {
            return null;
        }

        return create(ident, facet, tmpl.getMode(), tmpl.getSecure(), null, adapterId, null);
    }

    public Reference
    create(Ice.Identity ident, Ice.ConnectionI fixedConnection)
    {
        if(ident.name.length() == 0 && ident.category.length() == 0)
        {
            return null;
        }

        //
        // Create new reference
        //
        FixedReference ref = new FixedReference(_instance, 
                                                _communicator, 
                                                ident, 
                                                _instance.getDefaultContext(),
                                                "", // Facet
                                                Reference.ModeTwoway,
                                                false,
                                                fixedConnection);
        return updateCache(ref);
    }

    public Reference
    copy(Reference r)
    {
        Ice.Identity ident = r.getIdentity();
        if(ident.name.length() == 0 && ident.category.length() == 0)
        {
            return null;
        }
        return (Reference)r.clone();
    }

    public Reference
    create(String s, String propertyPrefix)
    {
        if(s == null || s.length() == 0)
        {
            return null;
        }

        final String delim = " \t\n\r";

        int beg;
        int end = 0;

        beg = IceUtilInternal.StringUtil.findFirstNotOf(s, delim, end);
        if(beg == -1)
        {
            Ice.ProxyParseException e = new Ice.ProxyParseException();
            e.str = s;
            throw e;
        }

        //
        // Extract the identity, which may be enclosed in single
        // or double quotation marks.
        //
        String idstr = null;
        end = IceUtilInternal.StringUtil.checkQuote(s, beg);
        if(end == -1)
        {
            Ice.ProxyParseException e = new Ice.ProxyParseException();
            e.str = s;
            throw e;
        }
        else if(end == 0)
        {
            end = IceUtilInternal.StringUtil.findFirstOf(s, delim + ":@", beg);
            if(end == -1)
            {
                end = s.length();
            }
            idstr = s.substring(beg, end);
        }
        else
        {
            beg++; // Skip leading quote
            idstr = s.substring(beg, end);
            end++; // Skip trailing quote
        }

        if(beg == end)
        {
            Ice.ProxyParseException e = new Ice.ProxyParseException();
            e.str = s;
            throw e;
        }

        //
        // Parsing the identity may raise IdentityParseException.
        //
        Ice.Identity ident = _instance.stringToIdentity(idstr);

        if(ident.name.length() == 0)
        {
            //
            // An identity with an empty name and a non-empty
            // category is illegal.
            //
            if(ident.category.length() > 0)
            {
                Ice.IllegalIdentityException e = new Ice.IllegalIdentityException();
                e.id = ident;
                throw e;
            }
            //
            // Treat a stringified proxy containing two double
            // quotes ("") the same as an empty string, i.e.,
            // a null proxy, but only if nothing follows the
            // quotes.
            //
            else if(IceUtilInternal.StringUtil.findFirstNotOf(s, delim, end) != -1)
            {
                Ice.ProxyParseException e = new Ice.ProxyParseException();
                e.str = s;
                throw e;
            }
            else
            {
                return null;
            }
        }

        String facet = "";
        int mode = Reference.ModeTwoway;
        boolean secure = false;
        String adapter = "";

        while(true)
        {
            beg = IceUtilInternal.StringUtil.findFirstNotOf(s, delim, end);
            if(beg == -1)
            {
                break;
            }

            if(s.charAt(beg) == ':' || s.charAt(beg) == '@')
            {
                break;
            }

            end = IceUtilInternal.StringUtil.findFirstOf(s, delim + ":@", beg);
            if(end == -1)
            {
                end = s.length();
            }

            if(beg == end)
            {
                break;
            }

            String option = s.substring(beg, end);
            if(option.length() != 2 || option.charAt(0) != '-')
            {
                Ice.ProxyParseException e = new Ice.ProxyParseException();
                e.str = s;
                throw e;
            }

            //
            // Check for the presence of an option argument. The
            // argument may be enclosed in single or double
            // quotation marks.
            //
            String argument = null;
            int argumentBeg = IceUtilInternal.StringUtil.findFirstNotOf(s, delim, end);
            if(argumentBeg != -1)
            {
                final char ch = s.charAt(argumentBeg);
                if(ch != '@' && ch != ':' && ch != '-')
                {
                    beg = argumentBeg;
                    end = IceUtilInternal.StringUtil.checkQuote(s, beg);
                    if(end == -1)
                    {
                        Ice.ProxyParseException e = new Ice.ProxyParseException();
                        e.str = s;
                        throw e;
                    }
                    else if(end == 0)
                    {
                        end = IceUtilInternal.StringUtil.findFirstOf(s, delim + ":@", beg);
                        if(end == -1)
                        {
                            end = s.length();
                        }
                        argument = s.substring(beg, end);
                    }
                    else
                    {
                        beg++; // Skip leading quote
                        argument = s.substring(beg, end);
                        end++; // Skip trailing quote
                    }
                }
            }

            //
            // If any new options are added here,
            // IceInternal::Reference::toString() and its derived classes must be updated as well.
            //
            switch(option.charAt(1))
            {
                case 'f':
                {
                    if(argument == null)
                    {
                        Ice.ProxyParseException e = new Ice.ProxyParseException();
                        e.str = s;
                        throw e;
                    }

                    Ice.StringHolder facetH = new Ice.StringHolder();
                    if(!IceUtilInternal.StringUtil.unescapeString(argument, 0, argument.length(), facetH))
                    {
                        Ice.ProxyParseException e = new Ice.ProxyParseException();
                        e.str = s;
                        throw e;
                    }

                    facet = facetH.value;
                    break;
                }

                case 't':
                {
                    if(argument != null)
                    {
                        Ice.ProxyParseException e = new Ice.ProxyParseException();
                        e.str = s;
                        throw e;
                    }
                    mode = Reference.ModeTwoway;
                    break;
                }

                case 'o':
                {
                    if(argument != null)
                    {
                        Ice.ProxyParseException e = new Ice.ProxyParseException();
                        e.str = s;
                        throw e;
                    }
                    mode = Reference.ModeOneway;
                    break;
                }

                case 'O':
                {
                    if(argument != null)
                    {
                        Ice.ProxyParseException e = new Ice.ProxyParseException();
                        e.str = s;
                        throw e;
                    }
                    mode = Reference.ModeBatchOneway;
                    break;
                }

                case 'd':
                {
                    if(argument != null)
                    {
                        Ice.ProxyParseException e = new Ice.ProxyParseException();
                        e.str = s;
                        throw e;
                    }
                    mode = Reference.ModeDatagram;
                    break;
                }

                case 'D':
                {
                    if(argument != null)
                    {
                        Ice.ProxyParseException e = new Ice.ProxyParseException();
                        e.str = s;
                        throw e;
                    }
                    mode = Reference.ModeBatchDatagram;
                    break;
                }

                case 's':
                {
                    if(argument != null)
                    {
                        Ice.ProxyParseException e = new Ice.ProxyParseException();
                        e.str = s;
                        throw e;
                    }
                    secure = true;
                    break;
                }

                default:
                {
                    Ice.ProxyParseException e = new Ice.ProxyParseException();
                    e.str = s;
                    throw e;
                }
            }
        }

        if(beg == -1)
        {
            return create(ident, facet, mode, secure, null, null, propertyPrefix);
        }

        java.util.ArrayList<EndpointI> endpoints = new java.util.ArrayList<EndpointI>();

        if(s.charAt(beg) == ':')
        {
            java.util.ArrayList<String> unknownEndpoints = new java.util.ArrayList<String>();
            end = beg;

            while(end < s.length() && s.charAt(end) == ':')
            {
                beg = end + 1;
                
                end = beg;
                while(true)
                {
                    end = s.indexOf(':', end);
                    if(end == -1)
                    {
                        end = s.length();
                        break;
                    }
                    else
                    {
                        boolean quoted = false;
                        int quote = beg;
                        while(true)
                        {
                            quote = s.indexOf('\"', quote);
                            if(quote == -1 || end < quote)
                            {
                                break;
                            }
                            else
                            {
                                quote = s.indexOf('\"', ++quote);
                                if(quote == -1)
                                {
                                    break;
                                }
                                else if(end < quote)
                                {
                                    quoted = true;
                                    break;
                                }
                                ++quote;
                            }
                        }
                        if(!quoted)
                        {
                            break;
                        }
                        ++end;
                    }
                }
                
                String es = s.substring(beg, end);
                EndpointI endp = _instance.endpointFactoryManager().create(es, false);
                if(endp != null)
                {
                    endpoints.add(endp);
                }
                else
                {
                    unknownEndpoints.add(es);
                }
            }
            if(endpoints.size() == 0)
            {
                Ice.EndpointParseException e = new Ice.EndpointParseException();
                e.str = unknownEndpoints.get(0);
                throw e;
            }
            else if(unknownEndpoints.size() != 0 &&
                   _instance.initializationData().properties.getPropertyAsIntWithDefault("Ice.Warn.Endpoints", 1) > 0)
            {
                String msg = "Proxy contains unknown endpoints:";
                java.util.Iterator<String> iter = unknownEndpoints.iterator();
                while(iter.hasNext())
                {
                    msg += " `" + iter.next() + "'";
                }
                _instance.initializationData().logger.warning(msg);
            }

            EndpointI[] endp = new EndpointI[endpoints.size()];
            endpoints.toArray(endp);
            return create(ident, facet, mode, secure, endp, null, propertyPrefix);
        }
        else if(s.charAt(beg) == '@')
        {
            beg = IceUtilInternal.StringUtil.findFirstNotOf(s, delim, beg + 1);
            if(beg == -1)
            {
                Ice.ProxyParseException e = new Ice.ProxyParseException();
                e.str = s;
                throw e;
            }

            String adapterstr = null;
            end = IceUtilInternal.StringUtil.checkQuote(s, beg);
            if(end == -1)
            {
                Ice.ProxyParseException e = new Ice.ProxyParseException();
                e.str = s;
                throw e;
            }
            else if(end == 0)
            {
                end = IceUtilInternal.StringUtil.findFirstOf(s, delim, beg);
                if(end == -1)
                {
                    end = s.length();
                }
                adapterstr = s.substring(beg, end);
            }
            else
            {
                beg++; // Skip leading quote
                adapterstr = s.substring(beg, end);
                end++; // Skip trailing quote
            }

            if(end != s.length() && IceUtilInternal.StringUtil.findFirstNotOf(s, delim, end) != -1)
            {
                Ice.ProxyParseException e = new Ice.ProxyParseException();
                e.str = s;
                throw e;
            }
            
            Ice.StringHolder token = new Ice.StringHolder();
            if(!IceUtilInternal.StringUtil.unescapeString(adapterstr, 0, adapterstr.length(), token) ||
               token.value.length() == 0)
            {
                Ice.ProxyParseException e = new Ice.ProxyParseException();
                e.str = s;
                throw e;
            }
            adapter = token.value;
            return create(ident, facet, mode, secure, null, adapter, propertyPrefix);
        }

        Ice.ProxyParseException ex = new Ice.ProxyParseException();
        ex.str = s;
        throw ex;
    }

    public Reference
    create(Ice.Identity ident, BasicStream s)
    {
        //
        // Don't read the identity here. Operations calling this
        // constructor read the identity, and pass it as a parameter.
        //

        if(ident.name.length() == 0 && ident.category.length() == 0)
        {
            return null;
        }

        //
        // For compatibility with the old FacetPath.
        //
        String[] facetPath = s.readStringSeq();
        String facet;
        if(facetPath.length > 0)
        {
            if(facetPath.length > 1)
            {
                throw new Ice.ProxyUnmarshalException();
            }
            facet = facetPath[0];
        }
        else
        {
            facet = "";
        }

        int mode = (int)s.readByte();
        if(mode < 0 || mode > Reference.ModeLast)
        {
            throw new Ice.ProxyUnmarshalException();
        }

        boolean secure = s.readBool();

        EndpointI[] endpoints = null;
        String adapterId = null;

        int sz = s.readSize();
        if(sz > 0)
        {
            endpoints = new EndpointI[sz];
            for(int i = 0; i < sz; i++)
            {
                endpoints[i] = _instance.endpointFactoryManager().read(s);
            }
        }
        else
        {
            adapterId = s.readString();
        }

        return create(ident, facet, mode, secure, endpoints, adapterId, null);
    }

    public ReferenceFactory
    setDefaultRouter(Ice.RouterPrx defaultRouter)
    {
        if(_defaultRouter == null ? defaultRouter == null : _defaultRouter.equals(defaultRouter))
        {
            return this;
        }
        
        ReferenceFactory factory = new ReferenceFactory(_instance, _communicator);
        factory._defaultLocator = _defaultLocator;
        factory._defaultRouter = defaultRouter;
        return factory;
    }

    public Ice.RouterPrx
    getDefaultRouter()
    {
        return _defaultRouter;
    }

    public ReferenceFactory
    setDefaultLocator(Ice.LocatorPrx defaultLocator)
    {
        if(_defaultLocator == null ? defaultLocator == null : _defaultLocator.equals(defaultLocator))
        {
            return this;
        }
        
        ReferenceFactory factory = new ReferenceFactory(_instance, _communicator);
        factory._defaultRouter = _defaultRouter;
        factory._defaultLocator = defaultLocator;
        return factory;
    }

    public Ice.LocatorPrx
    getDefaultLocator()
    {
        return _defaultLocator;
    }

    //
    // Only for use by Instance
    //
    ReferenceFactory(Instance instance, Ice.Communicator communicator)
    {
        _instance = instance;
        _communicator = communicator;
    }

    synchronized void
    destroy()
    {
        _references.clear();
    }

    synchronized private Reference
    updateCache(Reference ref)
    {
        //
        // If we already have an equivalent reference, use such equivalent
        // reference. Otherwise add the new reference to the reference
        // set.
        //
        // Java implementation note: A WeakHashMap is used to hold References,
        // allowing References to be garbage collected automatically. A
        // Reference serves as both key and value in the map. The
        // WeakHashMap class internally creates a weak reference for the
        // key, and we use a weak reference for the value as well.
        //
        java.lang.ref.WeakReference<Reference> w = _references.get(ref);
        if(w != null)
        {
            Reference r = w.get();
            if(r != null)
            {
                ref = r;
            }
            else
            {
                _references.put(ref, new java.lang.ref.WeakReference<Reference>(ref));
            }
        }
        else
        {
            _references.put(ref, new java.lang.ref.WeakReference<Reference>(ref));
        }

        return ref;
    }

    static private String[] _suffixes =
    {
        "EndpointSelection",
        "ConnectionCached",
        "PreferSecure",
        "LocatorCacheTimeout",
        "Locator",
        "Router",
        "CollocationOptimized"
    };

    private void
    checkForUnknownProperties(String prefix)
    {
        //
        // Do not warn about unknown properties if Ice prefix, ie Ice, Glacier2, etc
        //      
        for(int i = 0; IceInternal.PropertyNames.clPropNames[i] != null; ++i)
        {
            if(prefix.startsWith(IceInternal.PropertyNames.clPropNames[i] + "."))
            {
                return;
            }
        }

        java.util.ArrayList<String> unknownProps = new java.util.ArrayList<String>();
        java.util.Map<String, String> props =
            _instance.initializationData().properties.getPropertiesForPrefix(prefix + ".");
        java.util.Iterator<java.util.Map.Entry<String, String> > p = props.entrySet().iterator();
        while(p.hasNext())
        {
            java.util.Map.Entry<String, String> entry = p.next();
            String prop = entry.getKey();

            boolean valid = false;
            for(int i = 0; i < _suffixes.length; ++i)
            {
                if(prop.equals(prefix + "." + _suffixes[i]))
                {
                    valid = true;
                    break;
                }
            }

            if(!valid)
            {
                unknownProps.add(prop);
            }
        }

        if(unknownProps.size() != 0)
        {
            String message = "found unknown properties for proxy '" + prefix + "':";
            java.util.Iterator<String> q = unknownProps.iterator();
            while(q.hasNext())
            {
                message += "\n    " + q.next();
            }
            _instance.initializationData().logger.warning(message);
        }
    }

    private Reference
    create(Ice.Identity ident, String facet, int mode, boolean secure, EndpointI[] endpoints, String adapterId,
           String propertyPrefix)
    {
        DefaultsAndOverrides defaultsAndOverrides = _instance.defaultsAndOverrides();

        //
        // Default local proxy options.
        //
        LocatorInfo locatorInfo = _instance.locatorManager().get(_defaultLocator);
        RouterInfo routerInfo = _instance.routerManager().get(_defaultRouter);
        boolean collocationOptimized = defaultsAndOverrides.defaultCollocationOptimization;
        boolean cacheConnection = true;
        boolean preferSecure = defaultsAndOverrides.defaultPreferSecure;
        Ice.EndpointSelectionType endpointSelection = defaultsAndOverrides.defaultEndpointSelection;
        int locatorCacheTimeout = defaultsAndOverrides.defaultLocatorCacheTimeout;
        
        //
        // Override the defaults with the proxy properties if a property prefix is defined.
        //
        if(propertyPrefix != null && propertyPrefix.length() > 0)
        {
            Ice.Properties properties = _instance.initializationData().properties;

            //
            // Warn about unknown properties.
            //
            if(properties.getPropertyAsIntWithDefault("Ice.Warn.UnknownProperties", 1) > 0)
            {
                checkForUnknownProperties(propertyPrefix);
            }
            
            String property;
            
            property = propertyPrefix + ".Locator";
            Ice.LocatorPrx locator = Ice.LocatorPrxHelper.uncheckedCast(_communicator.propertyToProxy(property));
            if(locator != null)
            {
                locatorInfo = _instance.locatorManager().get(locator);
            }

            property = propertyPrefix + ".Router";
            Ice.RouterPrx router = Ice.RouterPrxHelper.uncheckedCast(_communicator.propertyToProxy(property));
            if(router != null)
            {
                if(propertyPrefix.endsWith(".Router"))
                {
                    String s = "`" + property + "=" + properties.getProperty(property) +
                        "': cannot set a router on a router; setting ignored";
                    _instance.initializationData().logger.warning(s);
                }
                else
                {
                    routerInfo = _instance.routerManager().get(router);
                }
            }
    
            property = propertyPrefix + ".CollocationOptimized";
            collocationOptimized = properties.getPropertyAsIntWithDefault(property, collocationOptimized ? 1 : 0) > 0;

            property = propertyPrefix + ".ConnectionCached";
            cacheConnection = properties.getPropertyAsIntWithDefault(property, cacheConnection ? 1 : 0) > 0;

            property = propertyPrefix + ".PreferSecure";
            preferSecure = properties.getPropertyAsIntWithDefault(property, preferSecure ? 1 : 0) > 0;

            property = propertyPrefix + ".EndpointSelection";
            if(properties.getProperty(property).length() > 0)
            {
                String type = properties.getProperty(property);
                if(type.equals("Random"))
                {
                    endpointSelection = Ice.EndpointSelectionType.Random;
                } 
                else if(type.equals("Ordered"))
                {
                    endpointSelection = Ice.EndpointSelectionType.Ordered;
                }
                else
                {
                    throw new Ice.EndpointSelectionTypeParseException(type);
                }
            }
        
            property = propertyPrefix + ".LocatorCacheTimeout";
            locatorCacheTimeout = properties.getPropertyAsIntWithDefault(property, locatorCacheTimeout);
        }
        
        //
        // Create new reference
        //
        return updateCache(new RoutableReference(_instance, 
                                                 _communicator,
                                                 ident,
                                                 _instance.getDefaultContext(), 
                                                 facet,
                                                 mode,
                                                 secure,
                                                 endpoints,
                                                 adapterId,
                                                 locatorInfo,
                                                 routerInfo,
                                                 collocationOptimized,
                                                 cacheConnection,
                                                 preferSecure,
                                                 endpointSelection,
                                                 locatorCacheTimeout));
    }

    final private Instance _instance;
    final private Ice.Communicator _communicator;
    private Ice.RouterPrx _defaultRouter;
    private Ice.LocatorPrx _defaultLocator;
    private java.util.WeakHashMap<Reference, java.lang.ref.WeakReference<Reference> > _references =
        new java.util.WeakHashMap<Reference, java.lang.ref.WeakReference<Reference> >();
}