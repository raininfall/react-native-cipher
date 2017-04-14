using ReactNative.Bridge;
using System;
using System.Collections.Generic;
using Windows.ApplicationModel.Core;
using Windows.UI.Core;

namespace Com.Reactlibrary.RNCipher
{
    /// <summary>
    /// A module that allows JS to share data.
    /// </summary>
    class RNCipherModule : NativeModuleBase
    {
        /// <summary>
        /// Instantiates the <see cref="RNCipherModule"/>.
        /// </summary>
        internal RNCipherModule()
        {

        }

        /// <summary>
        /// The name of the native module.
        /// </summary>
        public override string Name
        {
            get
            {
                return "RNCipher";
            }
        }
    }
}
