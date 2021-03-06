package se.bjurr.ssfb.presentation;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static se.bjurr.ssfb.admin.dto.SsfbRepoSettingsDTOTransfomer.fromSsfbRepoSettings;
import static se.bjurr.ssfb.settings.SsfbRepoSettingsTransfomer.fromSsfbRepoSettingsDTO;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import se.bjurr.ssfb.admin.dto.SsfbRepoSettingsDTO;
import se.bjurr.ssfb.service.SettingsService;
import se.bjurr.ssfb.settings.SsfbRepoSettings;

import com.atlassian.sal.api.user.UserManager;
import com.google.common.base.Optional;

@Path("/projects/{projectKey}/repos/{repoSlug}/repoadmin")
public class RepoAdminServlet {
 private final SettingsService settingsService;
 private final UserManager userManager;

 public RepoAdminServlet(SettingsService settingsService, UserManager userManager) {
  this.settingsService = settingsService;
  this.userManager = userManager;
 }

 @GET
 @Produces(APPLICATION_JSON)
 public Response get(//
   @PathParam("projectKey") String projectKey, //
   @PathParam("repoSlug") String repoSlug) throws Exception {
  Optional<SsfbRepoSettings> repoSettings = settingsService.getSsfbSettings().findRepoSettings(projectKey, repoSlug);
  if (repoSettings.isPresent()) {
   SsfbRepoSettingsDTO entity = fromSsfbRepoSettings(repoSettings.get());
   return ok()//
     .type(APPLICATION_JSON_TYPE)//
     .entity(entity)//
     .build();
  } else {
   return status(NOT_FOUND)//
     .build();
  }
 }

 @POST
 @Consumes(APPLICATION_JSON)
 @Produces(TEXT_PLAIN)
 public Response post(//
   @PathParam("projectKey") String projectKey, //
   @PathParam("repoSlug") String repoSlug, //
   final SsfbRepoSettingsDTO ssfbRepoSettingsDTO) throws Exception {
  if (!userManager.isAdmin(userManager.getRemoteUserKey())) {
   return status(UNAUTHORIZED)//
     .build();
  }

  SsfbRepoSettings ssfbRepoSettings = fromSsfbRepoSettingsDTO(ssfbRepoSettingsDTO);
  settingsService.setSsfbSettings(projectKey, repoSlug, ssfbRepoSettings);
  return ok() //
    .build();
 }

}