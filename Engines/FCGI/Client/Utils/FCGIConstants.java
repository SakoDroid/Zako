package Engines.FCGI.Client.Utils;

public class FCGIConstants {

    public static final int FCGI_BEGIN_REQUEST      =  1,       /*  [in]                             */
    FCGI_ABORT_REQUEST      =  2,                               /* [in]  (not supported)             */
    FCGI_END_REQUEST        =  3,                               /* [out]                             */
    FCGI_PARAMS             =  4,                               /* [in]  environment variables       */
    FCGI_STDIN              =  5,                               /* [in]  post data                   */
    FCGI_STDOUT             =  6,                               /* [out] response                    */
    FCGI_STDERR             =  7,                               /* [out] errors                      */
    FCGI_DATA               =  8,                               /* [in]  filter data (not supported) */
    FCGI_GET_VALUES         =  9,                               /* [in]                              */
    FCGI_GET_VALUES_RESULT  = 10;                               /* [out]                             */

    public static final int FCGI_ROLE_RESPONSER = 1,
    FCGI_ROLE_AUTHORIZER = 2,
    FCGI_ROLE_FILTER = 3;

    public static final int FCGI_REQUEST_COMPLETE = 0,
    FCGI_CANT_MPX_CONN = 1,
    FCGI_OVERLOADED = 2,
    FCGI_UNKNOWN_ROLE = 3;

    public static final int FCGI_REP_OK = 0,
    FCGI_REP_ERROR = 1,
    FCGI_REP_ERROR_CONTENT_LENGTH = 2,
    FCGI_REP_ERROR_IOEXCEPTION = 3;

}
