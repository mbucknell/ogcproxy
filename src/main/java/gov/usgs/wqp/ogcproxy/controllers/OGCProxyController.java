package gov.usgs.wqp.ogcproxy.controllers;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.usgs.wqp.ogcproxy.model.ogc.services.OGCServices;
import gov.usgs.wqp.ogcproxy.services.ConfigurationService;
import gov.usgs.wqp.ogcproxy.services.ProxyService;
import gov.usgs.wqp.ogcproxy.services.RESTService;
import org.springframework.http.MediaType;

@RestController
public class OGCProxyController {
	private static final Logger LOG = LoggerFactory.getLogger(OGCProxyController.class);

	private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private final Lock readLock = readWriteLock.readLock();
	private final Lock writeLock = readWriteLock.writeLock();

	private ProxyService proxyService;
	private RESTService restService;
	protected ConfigurationService configurationService;

	@Autowired
	public OGCProxyController(ProxyService proxyService,
			RESTService restService, ConfigurationService configurationService) {
		this.proxyService = proxyService;
		this.restService = restService;
		this.configurationService = configurationService;
	}

	/** 
	 * Root context of the Application
	 * 
	 * @return The splash page of the application.
	 */

	@GetMapping({"/schemas/**", "/ows/**"})
	public void getSchemasAndOws(HttpServletRequest request, HttpServletResponse response) {
		LOG.info("OGCProxyController.getSchemasAndOws() - Performing request.");
		doProxy(request, response, OGCServices.WMS);
		LOG.info("OGCProxyController.getSchemasAndOws() - Done performing request.");
	}

	@GetMapping("/wms")
	public void wmsProxyGet(HttpServletRequest request, HttpServletResponse response) {
		LOG.info("OGCProxyController.wmsProxyGet() - Performing request.");
		proxyService.performRequest(request, response, OGCServices.WMS);
		LOG.info("OGCProxyController.wmsProxyGet() - Done performing request.");
	}

	@GetMapping("/wfs")
	public void wfsProxyGet(HttpServletRequest request, HttpServletResponse response) {
		LOG.info("OGCProxyController.wfsProxyGet() - Performing request.");
		doProxy(request, response, OGCServices.WFS);
		LOG.info("OGCProxyController.wfsProxyGet() - Done performing request.");
	}

	@PostMapping("/wms")
	public void wmsProxyPost(HttpServletRequest request, HttpServletResponse response) {
		LOG.info("OGCProxyController.wmsProxyPost() INFO - Performing request.");
		doProxy(request, response, OGCServices.WMS);
		LOG.info("OGCProxyController.wmsProxyPost() INFO - Done performing request.");
	}

	@PostMapping("/wfs")
	public void wfsProxyPost(HttpServletRequest request, HttpServletResponse response) {
		LOG.info("OGCProxyController.wfsProxyPost() - Performing request.");
		doProxy(request, response, OGCServices.WFS);
		LOG.info("OGCProxyController.wfsProxyPost() - Done performing request.");
	}

	@GetMapping(produces=MediaType.APPLICATION_JSON_VALUE, value="/rest/cachestatus/{site}")
	public Object restCacheStatus(@PathVariable String site) {
		LOG.info("OGCProxyController.restCacheStatus() - Performing request.");
		Object finalResult = new Object();
		try {
			if (readLock.tryLock(configurationService.getReadLockTimeout(), TimeUnit.SECONDS)) {
				try {
					finalResult = restService.checkCacheStatus(site);
				} catch(Throwable t) {
					LOG.error(t.getLocalizedMessage());
				} finally {
					readLock.unlock();
				}
			}
		} catch (InterruptedException e) {
			LOG.info("Unable to get read lock: " + e.getLocalizedMessage());
		}
		LOG.info("OGCProxyController.restCacheStatus() - Done performing request.");
		return finalResult;
	}

	@DeleteMapping("/rest/clearcache/{site}")
	public void restClearCache(@PathVariable String site, HttpServletResponse response) {
		LOG.info("OGCProxyController.restClearCache() - Performing request.");
		try {
			if (writeLock.tryLock(configurationService.getWriteLockTimeout(), TimeUnit.SECONDS)) {
				try {
					if (restService.clearCacheBySite(site)) {
						response.setStatus(HttpStatus.SC_OK);
					} else {
						response.setStatus(HttpStatus.SC_BAD_REQUEST);
					}
				} catch(Throwable t) {
					LOG.error(t.getLocalizedMessage());
					response.setStatus(HttpStatus.SC_BAD_REQUEST);
				} finally {
					writeLock.unlock();
				}
			} else {
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		} catch (InterruptedException e) {
			LOG.info("Unable to get read lock: " + e.getLocalizedMessage());
		}
		LOG.info("OGCProxyController.restClearCache() - Done performing request.");
	}

	public void doProxy(HttpServletRequest request, HttpServletResponse response, OGCServices ogcService) {
		try {
			if (readLock.tryLock(configurationService.getWriteLockTimeout(), TimeUnit.SECONDS)) {
				try {
					proxyService.performRequest(request, response, ogcService);
				} catch(Throwable t) {
					LOG.error(t.getLocalizedMessage());
				} finally {
					readLock.unlock();
				}
			} else {
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		} catch (InterruptedException e) {
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			LOG.info("Unable to get read lock: " + e.getLocalizedMessage());
		}
	}
}
