/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2008-present Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.blobstore.s3.internal;

import java.util.Arrays;
import java.util.List;

import javax.inject.Named;

import org.sonatype.goodies.i18n.I18N;
import org.sonatype.goodies.i18n.MessageBundle;
import org.sonatype.nexus.blobstore.BlobStoreDescriptor;
import org.sonatype.nexus.formfields.ComboboxFormField;
import org.sonatype.nexus.formfields.CheckboxFormField;
import org.sonatype.nexus.formfields.FormField;
import org.sonatype.nexus.formfields.NumberTextFormField;
import org.sonatype.nexus.formfields.PasswordFormField;
import org.sonatype.nexus.formfields.StringTextFormField;

/**
 * A {@link BlobStoreDescriptor} for {@link S3BlobStore}.
 *
 * @since 3.6.1
 */
@Named(S3BlobStoreDescriptor.TYPE)
public class S3BlobStoreDescriptor
    implements BlobStoreDescriptor
{
  public static final String TYPE = "S3";

  private interface Messages
      extends MessageBundle
  {
    @DefaultMessage("S3")
    String name();

    @DefaultMessage("Bucket")
    String bucketLabel();

    @DefaultMessage("S3 Bucket Name (must be between 3 and 63 characters long containing only lower-case characters, numbers, periods, and dashes)")
    String bucketHelp();

    @DefaultMessage("Prefix")
    String prefixLabel();

    @DefaultMessage("S3 Path prefix")
    String prefixHelp();

    @DefaultMessage("Access Key ID (Optional)")
    String accessKeyIdLabel();

    @DefaultMessage("The AWS Access Key ID used for authentication when IAM roles are not being used")
    String accessKeyIdHelp();

    @DefaultMessage("Secret Access Key (Optional)")
    String secretAccessKeyLabel();

    @DefaultMessage("The AWS Secret Access Key used for authentication when IAM roles are not being used")
    String secretAccessKeyHelp();

    @DefaultMessage("Session Token (Optional)")
    String sessionTokenLabel();

    @DefaultMessage("An STS Session Token, if required")
    String sessionTokenHelp();

    @DefaultMessage("Assume Role ARN (Optional)")
    String assumeRoleLabel();

    @DefaultMessage("Optional ARN for Role to Assume, if required")
    String assumeRoleHelp();

    @DefaultMessage("Region")
    String regionLabel();

    @DefaultMessage("The AWS Region to use")
    String regionHelp();

    @DefaultMessage("Endpoint URL (Optional)")
    String endpointLabel();

    @DefaultMessage("A custom endpoint URL for third party object stores using the S3 API")
    String endpointHelp();

    @DefaultMessage("Expiration Days")
    String expirationLabel();

    @DefaultMessage("How many days until deleted blobs are finally removed from the S3 bucket (-1 to disable)")
    String expirationHelp();

    @DefaultMessage("Signature Version")
    String signerTypeLabel();

    @DefaultMessage("An API signature version which may be required for third party object stores using the S3 API")
    String signerTypeHelp();

    @DefaultMessage("Configures the client to use path-style access")
    String forcePathStyleLabel();

    @DefaultMessage("Setting this flag will result in path-style access being used for all requests")
    String forcePathStyleHelp();
  }

  private static final Messages messages = I18N.create(Messages.class);

  private final FormField bucket;
  private final FormField prefix;
  private final FormField accessKeyId;
  private final FormField secretAccessKey;
  private final FormField sessionToken;
  private final FormField assumeRole;
  private final FormField region;
  private final FormField endpoint;
  private final FormField expiration;
  private final FormField signerType;
  private final FormField forcePathStyle;

  public S3BlobStoreDescriptor() {
    this.bucket = new StringTextFormField(
        S3BlobStore.BUCKET_KEY,
        messages.bucketLabel(),
        messages.bucketHelp(),
        FormField.MANDATORY,
        S3BlobStore.BUCKET_REGEX
    );
    this.prefix = new StringTextFormField(
        S3BlobStore.BUCKET_PREFIX,
        messages.prefixLabel(),
        messages.prefixHelp(),
        FormField.OPTIONAL
    );
    this.accessKeyId = new StringTextFormField(
        S3BlobStore.ACCESS_KEY_ID_KEY,
        messages.accessKeyIdLabel(),
        messages.accessKeyIdHelp(),
        FormField.OPTIONAL
    );
    this.secretAccessKey = new PasswordFormField(
        S3BlobStore.SECRET_ACCESS_KEY_KEY,
        messages.secretAccessKeyLabel(),
        messages.secretAccessKeyHelp(),
        FormField.OPTIONAL
    );
    this.assumeRole = new StringTextFormField(
        S3BlobStore.ASSUME_ROLE_KEY,
        messages.assumeRoleLabel(),
        messages.assumeRoleHelp(),
        FormField.OPTIONAL
    );
    this.sessionToken = new StringTextFormField(
        S3BlobStore.SESSION_TOKEN_KEY,
        messages.sessionTokenLabel(),
        messages.sessionTokenHelp(),
        FormField.OPTIONAL
    );
    this.region = new ComboboxFormField<String>(
        S3BlobStore.REGION_KEY,
        messages.regionLabel(),
        messages.regionHelp(),
        FormField.MANDATORY,
        AmazonS3Factory.DEFAULT
    ).withStoreApi("s3_S3.regions");
    this.region.getAttributes().put("sortProperty", "order");
    this.endpoint = new StringTextFormField(
        S3BlobStore.ENDPOINT_KEY,
        messages.endpointLabel(),
        messages.endpointHelp(),
        FormField.OPTIONAL
    );
    this.expiration = new NumberTextFormField(
        S3BlobStore.EXPIRATION_KEY,
        messages.expirationLabel(),
        messages.expirationHelp(),
        FormField.OPTIONAL)
        .withInitialValue(S3BlobStore.DEFAULT_EXPIRATION_IN_DAYS)
        .withMinimumValue(-1);
    this.signerType = new ComboboxFormField<String>(
        S3BlobStore.SIGNERTYPE_KEY,
        messages.signerTypeLabel(),
        messages.signerTypeHelp(),
        FormField.MANDATORY,
        AmazonS3Factory.DEFAULT
    ).withStoreApi("s3_S3.signertypes");
    this.signerType.getAttributes().put("sortProperty", "order");
    this.forcePathStyle = new CheckboxFormField(
        S3BlobStore.FORCE_PATH_STYLE_KEY,
        messages.forcePathStyleLabel(),
        messages.forcePathStyleHelp(),
        FormField.MANDATORY
    ).withInitialValue(false);
  }

  @Override
  public String getName() {
    return messages.name();
  }

  @Override
  public List<FormField> getFormFields() {
    return Arrays.asList(bucket, prefix, accessKeyId, secretAccessKey, sessionToken, assumeRole, region, endpoint,
        expiration, signerType, forcePathStyle);
  }
}
