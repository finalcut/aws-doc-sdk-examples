//snippet-sourcedescription:[DeleteDataSource.kt demonstrates how to delete an Amazon Kendra data source.]
//snippet-keyword:[SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Kendra]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[03/10/2022]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.kendra

// snippet-start:[kendra.kotlin.delete.datasource.import]
import aws.sdk.kotlin.services.kendra.KendraClient
import aws.sdk.kotlin.services.kendra.model.DeleteDataSourceRequest
import kotlin.system.exitProcess
// snippet-end:[kendra.kotlin.delete.datasource.import]

/**
To run this Kotlin code example, ensure that you have set up your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {

    val usage = """
        Usage:
            <dataSourceId> <indexId> 

        Where:
            dataSourceId - The id value of the data source.
            indexId - The id value of the index.
            
    """

    if (args.size != 2) {
        println(usage)
        exitProcess(1)
     }

    val dataSourceId = args[0]
    val indexId = args[1]
    deleteSpecificDataSource(indexId, dataSourceId)
}

// snippet-start:[kendra.kotlin.delete.datasource.main]
suspend fun deleteSpecificDataSource(indexIdVal: String?, dataSourceId: String) {

        val dataSourceRequest = DeleteDataSourceRequest {
            id = dataSourceId
            indexId = indexIdVal
        }

        KendraClient { region = "us-east-1" }.use { kendra ->
            kendra.deleteDataSource(dataSourceRequest)
            println("$dataSourceId was successfully deleted.")
        }
}
// snippet-end:[kendra.kotlin.delete.datasource.main]