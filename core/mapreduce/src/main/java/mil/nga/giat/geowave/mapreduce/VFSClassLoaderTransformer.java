package mil.nga.giat.geowave.mapreduce;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.impl.VFSClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.giat.geowave.core.store.spi.ClassLoaderTransformerSpi;

public class VFSClassLoaderTransformer implements
		ClassLoaderTransformerSpi
{
	private static final Logger LOGGER = LoggerFactory.getLogger(VFSClassLoaderTransformer.class);

	@Override
	public ClassLoader transform(
			final ClassLoader classLoader ) {
		if (classLoader instanceof VFSClassLoader) {
			final VFSClassLoader cl = (VFSClassLoader) classLoader;
			final FileObject[] fileObjs = cl.getFileObjects();
			final ArrayList<URL> fileList = new ArrayList();

			for (int i = 0; i < fileObjs.length; i++) {
				final String fileStr = fileObjs[i].toString();
				if (URLClassloaderUtils.verifyProtocol(fileStr)) {
					try {
						fileList.add(new URL(
								fileStr));
					}
					catch (final MalformedURLException e) {
						LOGGER.error(
								"Unable to register classloader for '" + fileStr + "'",
								e);
					}
				}
				else {
					LOGGER.error("Failed to register class loader from: " + fileStr);
				}
			}

			final URL[] fileUrls = new URL[fileList.size()];
			for (int i = 0; i < fileList.size(); i++) {
				fileUrls[i] = fileList.get(i);
			}

			return java.security.AccessController.doPrivileged(new java.security.PrivilegedAction<URLClassLoader>() {
				@Override
				public URLClassLoader run() {
					final URLClassLoader ucl = new URLClassLoader(
							fileUrls,
							cl);
					return ucl;
				}
			});
		}
		return null;
	}

}
