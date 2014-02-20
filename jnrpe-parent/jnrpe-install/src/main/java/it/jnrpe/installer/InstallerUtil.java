package it.jnrpe.installer;

import java.io.InputStream;

public class InstallerUtil {

    public final static boolean ROOT = _init();

    private static boolean _init() {

        try {
            Process p = Runtime.getRuntime().exec("id -u");
            p.waitFor();

            byte[] buff = new byte[50];
            InputStream in = p.getInputStream();

            StringBuffer res = new StringBuffer();
            int iCount;

            while ((iCount = in.read(buff)) > 0) {
                res.append(new String(buff, 0, iCount));
            }

            return res.toString().trim().equals("0");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
