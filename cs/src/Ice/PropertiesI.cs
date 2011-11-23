// **********************************************************************
//
// Copyright (c) 2003-2009 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

using System.Collections;
using System.Collections.Generic;
using System.Diagnostics;
using System.Text.RegularExpressions;

namespace Ice
{
    sealed class PropertiesI :  Properties
    {
        class PropertyValue
        {
            public PropertyValue(PropertyValue v)
            {
                val = v.val;
                used = v.used;
            }

            public PropertyValue(string v, bool u)
            {
                val = v;
                used = u;
            }

            public string val;
            public bool used;
        }

        public string getProperty(string key)
        {
            lock(this)
            {
                string result = "";
                PropertyValue pv = (PropertyValue)_properties[key];
                if(pv != null)
                {
                    pv.used = true;
                    result = pv.val;
                }
                return result;
            }
        }
        
        public string getPropertyWithDefault(string key, string val)
        {
            lock(this)
            {
                string result = val;
                PropertyValue pv = (PropertyValue)_properties[key];
                if(pv != null)
                {
                    pv.used = true;
                    result = pv.val;
                }
                return result;
            }
        }
        
        public int getPropertyAsInt(string key)
        {
            return getPropertyAsIntWithDefault(key, 0);
        }
        
        public int getPropertyAsIntWithDefault(string key, int val)
        {
            lock(this)
            {
                PropertyValue pv = (PropertyValue)_properties[key];
                if(pv == null)
                {
                    return val;
                }
                else
                {
                    pv.used = true;
                    try
                    {
                        return System.Int32.Parse(pv.val);
                    }
                    catch(System.FormatException)
                    {
                        Ice.Util.getProcessLogger().warning("numeric property " + key + 
                                                            " set to non-numeric value, defaulting to " + val);
                        return val;
                    }
                }
            }
        }
        
        public string[] getPropertyAsList(string key)
        {
            return getPropertyAsListWithDefault(key, null);
        }
        
        public string[] getPropertyAsListWithDefault(string key, string[] val)
        {
            if(val == null)
            {
                val = new string[0];
            }

            lock(this)
            {
                PropertyValue pv = (PropertyValue)_properties[key];
                if(pv == null)
                {
                    return val;
                }
                else
                {
                    pv.used = true;

                    string[] result = splitString(pv.val, ", \t\r\n");
                    if(result == null)
                    {
                        Ice.Util.getProcessLogger().warning("mismatched quotes in property " + key 
                                                            + "'s value, returning default value");
                        return val;
                    }
                    else
                    {
                        return result;
                    }
                }
            }
        }


        public Dictionary<string, string> getPropertiesForPrefix(string prefix)
        {
            lock(this)
            {
                Dictionary<string, string> result = new Dictionary<string, string>();

                foreach(string s in _properties.Keys)
                {
                    if(prefix.Length == 0 || s.StartsWith(prefix))
                    {
                        PropertyValue pv = (PropertyValue)_properties[s];
                        pv.used = true;
                        result[s] = pv.val;
                    }
                }
                return result;
            }
        }
        
        public void setProperty(string key, string val)
        {
            //
            // Trim whitespace
            //
            if(key != null)
            {
                key = key.Trim();
            }
            if(key == null || key.Length == 0)
            {
                throw new Ice.InitializationException("Attempt to set property with empty key");
            }

            //
            // Check if the property is legal.
            //
            Logger logger = Ice.Util.getProcessLogger();
            int dotPos = key.IndexOf('.');
            if(dotPos != -1)
            {
                string prefix = key.Substring(0, dotPos);
                for(int i = 0; IceInternal.PropertyNames.validProps[i] != null; ++i)
                {
                    string pattern = IceInternal.PropertyNames.validProps[i][0].pattern();
                    dotPos = pattern.IndexOf('.');
                    Debug.Assert(dotPos != -1);
                    string propPrefix = pattern.Substring(1, dotPos - 2);
                    if(!propPrefix.Equals(prefix))
                    {
                        continue;
                    }

                    bool found = false;
                    for(int j = 0; IceInternal.PropertyNames.validProps[i][j] != null && !found; ++j)
                    {
                        Regex r = new Regex(IceInternal.PropertyNames.validProps[i][j].pattern());
                        Match m = r.Match(key);
                        found = m.Success;
                        if(found && IceInternal.PropertyNames.validProps[i][j].deprecated())
                        {
                            logger.warning("deprecated property: " + key);
                            if(IceInternal.PropertyNames.validProps[i][j].deprecatedBy() != null)
                            {
                                key = IceInternal.PropertyNames.validProps[i][j].deprecatedBy();
                            }
                        }
                    }
                    if(!found)
                    {
                        logger.warning("unknown property: " + key);
                    }
                }
            }

            lock(this)
            {
                //
                //
                // Set or clear the property.
                //
                if(val != null && val.Length > 0)
                {
                    PropertyValue pv = (PropertyValue)_properties[key];
                    if(pv != null)
                    {
                        pv.val = val;
                    }
                    else
                    {
                        pv = new PropertyValue(val, false);
                    }
                    _properties[key] = pv;
                }
                else
                {
                    _properties.Remove(key);
                }
            }
        }
        
        public string[] getCommandLineOptions()
        {
            lock(this)
            {
                string[] result = new string[_properties.Count];
                int i = 0;
                foreach(DictionaryEntry entry in _properties)
                {
                    result[i++] = "--" + entry.Key + "=" + ((PropertyValue)entry.Value).val;
                }
                return result;
            }
        }

        public string[] parseCommandLineOptions(string pfx, string[] options)
        {
            if(pfx.Length > 0 && pfx[pfx.Length - 1] != '.')
            {
                pfx += '.';
            }
            pfx = "--" + pfx;

            ArrayList result = new ArrayList();
            for(int i = 0; i < options.Length; i++)
            {
                string opt = options[i];
                if(opt.StartsWith(pfx))
                {
                    if(opt.IndexOf('=') == -1)
                    {
                        opt += "=1";
                    }

                    parseLine(opt.Substring(2));
                }
                else
                {
                    result.Add(opt);
                }
            }
            string[] arr = new string[result.Count];
            if(arr.Length != 0)
            {
                result.CopyTo(arr);
            }
            return arr;
        }
        
        public string[] parseIceCommandLineOptions(string[] options)
        {
            string[] args = options;
            for(int i = 0; IceInternal.PropertyNames.clPropNames[i] != null; ++i)
            {
                args = parseCommandLineOptions(IceInternal.PropertyNames.clPropNames[i], args);
            }
            return args;
        }
        
        public void load(string file)
        {
            try
            {
                using(System.IO.StreamReader sr = new System.IO.StreamReader(file))
                {
                    parse(sr);
                }
            }
            catch(System.IO.IOException ex)
            {
                Ice.FileException fe = new Ice.FileException(ex);
                fe.path = file;
                throw fe;
            }
        }
        
        public Properties ice_clone_()
        {
            lock(this)
            {
                return new PropertiesI(this);
            }
        }

        public ArrayList getUnusedProperties()
        {
            lock(this)
            {
                ArrayList unused = new ArrayList();
                foreach(DictionaryEntry entry in _properties)
                {
                    if(!((PropertyValue)entry.Value).used)
                    {
                        unused.Add(entry.Key);
                    }
                }
                return unused;
            }
        }
        
        internal PropertiesI(PropertiesI p)
        {
            _properties = new Hashtable();
            foreach(DictionaryEntry entry in p._properties)
            {
                _properties[entry.Key] = new PropertyValue((PropertyValue)entry.Value);
            }
        }

        internal PropertiesI()
        {
            _properties = new Hashtable();
        }
        
        internal PropertiesI(ref string[] args, Properties defaults)
        {
            if(defaults == null)
            {
                _properties = new Hashtable();
            }
            else
            {
                _properties = ((PropertiesI)defaults)._properties;
            }
            
            PropertyValue pv = (PropertyValue)_properties["Ice.ProgramName"];
            if(pv == null)
            {
                _properties["Ice.ProgramName"] = new PropertyValue(System.AppDomain.CurrentDomain.FriendlyName, true);
            }
            else
            {
                pv.used = true;
            }

            bool loadConfigFiles = false;

            for(int i = 0; i < args.Length; i++)
            {
                if(args[i].StartsWith("--Ice.Config"))
                {
                    string line = args[i];
                    if(line.IndexOf('=') == -1)
                    {
                        line += "=1";
                    }
                    parseLine(line.Substring(2));
                    loadConfigFiles = true;

                    string[] arr = new string[args.Length - 1];
                    System.Array.Copy(args, 0, arr, 0, i);
                    if(i < args.Length - 1)
                    {
                        System.Array.Copy(args, i + 1, arr, i, args.Length - i - 1);
                    }
                    args = arr;
                }
            }

            if(!loadConfigFiles)
            {
                //
                // If Ice.Config is not set, load from ICE_CONFIG (if set)
                //
                loadConfigFiles = (_properties["Ice.Config"] == null);
            }
            
            if(loadConfigFiles)
            {
                loadConfig();
            }
            
            args = parseIceCommandLineOptions(args); 
        }
        
        private void parse(System.IO.StreamReader input)
        {
            try
            {
                string line;
                while((line = input.ReadLine()) != null)
                {
                    parseLine(line);
                }
            }
            catch(System.IO.IOException ex)
            {
                SyscallException se = new SyscallException(ex);
                throw se;
            }
        }

        private const int ParseStateKey = 0;
        private const int ParseStateValue = 1;
        
        private void parseLine(string line)
        {
            string key = "";
            string val = "";

            int state = ParseStateKey;

            string whitespace = "";
            string escapedspace = "";
            bool finished = false;
            for(int i = 0; i < line.Length; ++i)
            {
                char c = line[i];
                switch(state)
                {
                  case ParseStateKey:
                  {
                      switch(c)
                      {
                        case '\\':
                          if(i < line.Length - 1)
                          {
                              c = line[++i];
                              switch(c)
                              {
                                case '\\':
                                case '#':
                                case '=':
                                  key += whitespace;
                                  whitespace= "";
                                  key += c;
                                  break;
        
                                case ' ':
                                  if(key.Length != 0)
                                  {
                                      whitespace += c;
                                  }
                                  break;
        
                                default:
                                  key += whitespace;
                                  whitespace= "";
                                  key += '\\';
                                  key += c;
                                  break;
                              }
                          }
                          else
                          {
                              key += whitespace;
                              key += c;
                          }
                          break;
        
                        case ' ':
                        case '\t':
                        case '\r':
                        case '\n':
                            if(key.Length != 0)
                            {
                                whitespace += c;
                            }
                            break;
        
                        case '=':
                            whitespace= "";
                            state = ParseStateValue;
                            break;
        
                        case '#':
                            finished = true;
                            break;
        
                        default:
                            key += whitespace;
                            whitespace= "";
                            key += c;
                            break;
                      }
                      break;
                  }

                  case ParseStateValue:
                  {
                      switch(c)
                      {
                        case '\\':
                          if(i < line.Length - 1)
                          {
                              c = line[++i];
                              switch(c)
                              {
                                case '\\':
                                case '#':
                                case '=':
                                  val += val.Length == 0 ? escapedspace : whitespace;
                                  whitespace= "";
                                  escapedspace= "";
                                  val += c;
                                  break;
        
                                case ' ':
                                  whitespace += c;
                                  escapedspace += c;
                                  break;
        
                                default:
                                  val += val.Length == 0 ? escapedspace : whitespace;
                                  whitespace= "";
                                  escapedspace= "";
                                  val += '\\';
                                  val += c;
                                  break;
                              }
                          }
                          else
                          {
                              val += val.Length == 0 ? escapedspace : whitespace;
                              val += c;
                          }
                          break;
        
                        case ' ':
                        case '\t':
                        case '\r':
                        case '\n':
                            if(val.Length != 0)
                            {
                                whitespace += c;
                            }
                            break;
        
                        case '#':
                            val += escapedspace;
                            finished = true;
                            break;
        
                        default:
                            val += val.Length == 0 ? escapedspace : whitespace;
                            whitespace = "";
                            escapedspace = "";
                            val += c;
                            break;
                      }
                      break;
                  }
                }
                if(finished)
                {
                    break;
                }
            }
            val += escapedspace;
        
            if((state == ParseStateKey && key.Length != 0) || (state == ParseStateValue && key.Length == 0))
            {
                Ice.Util.getProcessLogger().warning("invalid config file entry: \"" + line + "\"");
                return;
            }
            else if(key.Length == 0)
            {
                return;
            }

            setProperty(key, val);
        }
        
        private void loadConfig()
        {
            string val = getProperty("Ice.Config");
            
            if(val.Length == 0 || val.Equals("1"))
            {
                string s = System.Environment.GetEnvironmentVariable("ICE_CONFIG");
                if(s != null && s.Length != 0)
                {
                    val = s;
                }
            }

            if(val.Length > 0)
            {
                char[] separator = { ',' };
                string[] files = val.Split(separator);
                for(int i = 0; i < files.Length; i++)
                {
                    load(files[i]);
                }
            }
            
            _properties["Ice.Config"] = new PropertyValue(val, true);
        }
        
        //
        // Split string helper; returns null for unmatched quotes
        //
        private string[] splitString(string str, string delim)
        {
            ArrayList l = new ArrayList();
            char[] arr = new char[str.Length];
            int pos = 0;

            while(pos < str.Length)
            {
                int n = 0;
                char quoteChar = '\0';
                if(str[pos] == '"' || str[pos] == '\'')
                {
                    quoteChar = str[pos];
                    ++pos;
                }
                while(pos < str.Length)
                {
                    if(quoteChar != '\0' && str[pos] == '\\' && pos + 1 < str.Length && str[pos + 1] == quoteChar)
                    {
                        ++pos;
                    }
                    else if(quoteChar != '\0' && str[pos] == quoteChar)
                    {
                        ++pos;
                        quoteChar = '\0';
                        break;
                    }
                    else if(delim.IndexOf(str[pos]) != -1)
                    {
                        if(quoteChar == '\0')
                        {
                            ++pos;
                            break;
                        }
                    }
                
                    if(pos < str.Length)
                    {
                        arr[n++] = str[pos++];
                    }
                }
                if(quoteChar != '\0')
                {
                    return null; // Unmatched quote.
                }
                if(n > 0)
                {
                    l.Add(new string(arr, 0, n));
                }
            }

            return (string[])l.ToArray(typeof(string));
        }

        

        private Hashtable _properties;
    }
}