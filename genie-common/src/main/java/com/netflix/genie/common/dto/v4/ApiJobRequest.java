/*
 *
 *  Copyright 2018 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.genie.common.dto.v4;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.Getter;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * The builder and methods available for a request generated by a REST API request.
 *
 * @author tgianos
 * @since 4.0.0
 */
@JsonDeserialize(builder = ApiJobRequest.Builder.class)
public interface ApiJobRequest extends CommonRequest {
    /**
     * Get the timeout a user has requested for this job as number of seconds from job start that the server will
     * kill it.
     *
     * @return The timeout duration (in seconds) if one was requested wrapped in an {@link Optional}
     */
    Optional<Integer> getTimeout();

    /**
     * Get the command arguments a user has requested be appended to a command executable for their job.
     *
     * @return The command arguments as an immutable list. Any attempt to modify will throw exception
     */
    List<String> getCommandArgs();

    /**
     * Whether archiving of logs should be disabled or not.
     *
     * @return True if archiving is disabled
     */
    boolean isDisableArchiving();

    /**
     * Whether this job is interactive or not.
     *
     * @return True if the job is interactive
     */
    boolean isInteractive();

    /**
     * Get the metadata a user has supplied for the job including things like name, tags, etc.
     *
     * @return The metadata
     */
    JobMetadata getMetadata();

    /**
     * The resource criteria that was supplied for the job.
     *
     * @return The criteria used to select the cluster, command and optionally applications for the job
     */
    ExecutionResourceCriteria getCriteria();

    /**
     * The directory requested to place the job when the agent runs.
     *
     * @return The job directory wrapped in an {@link Optional}
     */
    Optional<File> getJobDirectoryLocation();

    /**
     * Builder for a V4 Job Request.
     *
     * @author tgianos
     * @since 4.0.0
     */
    @Getter(AccessLevel.PACKAGE)
    class Builder extends CommonRequestImpl.Builder<ApiJobRequest.Builder> {

        private final JobMetadata bMetadata;
        private final ExecutionResourceCriteria bCriteria;
        private ImmutableList<String> bCommandArgs;
        private Integer bTimeout;
        private boolean bDisableArchiving;
        private boolean bInteractive;
        private File bJobLocationDirectory;

        /**
         * Constructor with required parameters.
         *
         * @param metadata All user supplied metadata
         * @param criteria All user supplied execution criteria
         */
        @JsonCreator
        public Builder(
            @JsonProperty(value = "metadata", required = true) final JobMetadata metadata,
            @JsonProperty(value = "criteria", required = true) final ExecutionResourceCriteria criteria
        ) {
            super();
            this.bMetadata = metadata;
            this.bCriteria = criteria;
        }

        /**
         * Set the ordered list of command line arguments to append to the command executable at runtime.
         *
         * @param commandArgs The arguments in the order they should be placed on the command line. Maximum of 10,000
         *                    characters per argument
         * @return The builder
         */
        public Builder withCommandArgs(@Nullable final List<String> commandArgs) {
            this.bCommandArgs = commandArgs == null ? ImmutableList.of() : ImmutableList.copyOf(commandArgs);
            return this;
        }

        /**
         * Set the timeout (in seconds) that the job should be killed after by the service after it has started.
         *
         * @param timeout The timeout. Must be greater greater than or equal to 1 but preferably much higher
         * @return The builder
         */
        public Builder withTimeout(@Nullable final Integer timeout) {
            this.bTimeout = timeout;
            return this;
        }

        /**
         * Set whether to disable log archive for the job.
         *
         * @param disableArchival true if you want to disable log archival
         * @return The builder
         */
        public Builder withDisableArchiving(final boolean disableArchival) {
            this.bDisableArchiving = disableArchival;
            return this;
        }

        /**
         * Set whether the job should be treated as an interactive job or not.
         *
         * @param interactive true if the job is interactive
         * @return The builder
         */
        public Builder withInteractive(final boolean interactive) {
            this.bInteractive = interactive;
            return this;
        }

        /**
         * Set the directory location where to create the job working directory.
         *
         * @param jobDirectoryLocation the directory location
         * @return The builder
         */
        public Builder withJobDirectoryLocation(final File jobDirectoryLocation) {
            this.bJobLocationDirectory = jobDirectoryLocation;
            return this;
        }

        /**
         * Build an immutable job request instance.
         *
         * @return An immutable representation of the user supplied information for a job request
         */
        public ApiJobRequest build() {
            return new JobRequest(this);
        }
    }
}
