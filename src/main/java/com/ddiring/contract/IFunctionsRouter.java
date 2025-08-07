package com.ddiring.contract;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.StaticStruct;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint32;
import org.web3j.abi.datatypes.generated.Uint40;
import org.web3j.abi.datatypes.generated.Uint64;
import org.web3j.abi.datatypes.generated.Uint72;
import org.web3j.abi.datatypes.generated.Uint96;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/hyperledger-web3j/web3j/tree/main/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.13.0.
 */
@SuppressWarnings("rawtypes")
public class IFunctionsRouter extends Contract {
    public static final String BINARY = "";

    private static String librariesLinkedBinary;

    public static final String FUNC_FULFILL = "fulfill";

    public static final String FUNC_GETADMINFEE = "getAdminFee";

    public static final String FUNC_GETALLOWLISTID = "getAllowListId";

    public static final String FUNC_GETCONTRACTBYID = "getContractById";

    public static final String FUNC_GETPROPOSEDCONTRACTBYID = "getProposedContractById";

    public static final String FUNC_GETPROPOSEDCONTRACTSET = "getProposedContractSet";

    public static final String FUNC_ISVALIDCALLBACKGASLIMIT = "isValidCallbackGasLimit";

    public static final String FUNC_PAUSE = "pause";

    public static final String FUNC_PROPOSECONTRACTSUPDATE = "proposeContractsUpdate";

    public static final String FUNC_SENDREQUEST = "sendRequest";

    public static final String FUNC_SENDREQUESTTOPROPOSED = "sendRequestToProposed";

    public static final String FUNC_SETALLOWLISTID = "setAllowListId";

    public static final String FUNC_UNPAUSE = "unpause";

    public static final String FUNC_UPDATECONTRACTS = "updateContracts";

    @Deprecated
    protected IFunctionsRouter(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected IFunctionsRouter(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected IFunctionsRouter(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected IFunctionsRouter(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<TransactionReceipt> fulfill(byte[] response, byte[] err,
            BigInteger juelsPerGas, BigInteger costWithoutFulfillment, String transmitter,
            Commitment commitment) {
        final Function function = new Function(
                FUNC_FULFILL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(response), 
                new org.web3j.abi.datatypes.DynamicBytes(err), 
                new org.web3j.abi.datatypes.generated.Uint96(juelsPerGas), 
                new org.web3j.abi.datatypes.generated.Uint96(costWithoutFulfillment), 
                new org.web3j.abi.datatypes.Address(160, transmitter), 
                commitment), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> getAdminFee() {
        final Function function = new Function(FUNC_GETADMINFEE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint72>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<byte[]> getAllowListId() {
        final Function function = new Function(FUNC_GETALLOWLISTID, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<String> getContractById(byte[] id) {
        final Function function = new Function(FUNC_GETCONTRACTBYID, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(id)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> getProposedContractById(byte[] id) {
        final Function function = new Function(FUNC_GETPROPOSEDCONTRACTBYID, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(id)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<Tuple2<List<byte[]>, List<String>>> getProposedContractSet() {
        final Function function = new Function(FUNC_GETPROPOSEDCONTRACTSET, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Bytes32>>() {}, new TypeReference<DynamicArray<Address>>() {}));
        return new RemoteFunctionCall<Tuple2<List<byte[]>, List<String>>>(function,
                new Callable<Tuple2<List<byte[]>, List<String>>>() {
                    @Override
                    public Tuple2<List<byte[]>, List<String>> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<List<byte[]>, List<String>>(
                                convertToNative((List<Bytes32>) results.get(0).getValue()), 
                                convertToNative((List<Address>) results.get(1).getValue()));
                    }
                });
    }

    public RemoteFunctionCall<TransactionReceipt> pause() {
        final Function function = new Function(
                FUNC_PAUSE, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> proposeContractsUpdate(
            List<byte[]> proposalSetIds, List<String> proposalSetAddresses) {
        final Function function = new Function(
                FUNC_PROPOSECONTRACTSUPDATE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Bytes32>(
                        org.web3j.abi.datatypes.generated.Bytes32.class,
                        org.web3j.abi.Utils.typeMap(proposalSetIds, org.web3j.abi.datatypes.generated.Bytes32.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.datatypes.Address.class,
                        org.web3j.abi.Utils.typeMap(proposalSetAddresses, org.web3j.abi.datatypes.Address.class))), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> sendRequest(BigInteger subscriptionId,
            byte[] data, BigInteger dataVersion, BigInteger callbackGasLimit, byte[] donId) {
        final Function function = new Function(
                FUNC_SENDREQUEST, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint64(subscriptionId), 
                new org.web3j.abi.datatypes.DynamicBytes(data), 
                new org.web3j.abi.datatypes.generated.Uint16(dataVersion), 
                new org.web3j.abi.datatypes.generated.Uint32(callbackGasLimit), 
                new org.web3j.abi.datatypes.generated.Bytes32(donId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> sendRequestToProposed(BigInteger subscriptionId,
            byte[] data, BigInteger dataVersion, BigInteger callbackGasLimit, byte[] donId) {
        final Function function = new Function(
                FUNC_SENDREQUESTTOPROPOSED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint64(subscriptionId), 
                new org.web3j.abi.datatypes.DynamicBytes(data), 
                new org.web3j.abi.datatypes.generated.Uint16(dataVersion), 
                new org.web3j.abi.datatypes.generated.Uint32(callbackGasLimit), 
                new org.web3j.abi.datatypes.generated.Bytes32(donId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setAllowListId(byte[] allowListId) {
        final Function function = new Function(
                FUNC_SETALLOWLISTID, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(allowListId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> unpause() {
        final Function function = new Function(
                FUNC_UNPAUSE, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> updateContracts() {
        final Function function = new Function(
                FUNC_UPDATECONTRACTS, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static IFunctionsRouter load(String contractAddress, Web3j web3j,
            Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new IFunctionsRouter(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static IFunctionsRouter load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new IFunctionsRouter(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static IFunctionsRouter load(String contractAddress, Web3j web3j,
            Credentials credentials, ContractGasProvider contractGasProvider) {
        return new IFunctionsRouter(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static IFunctionsRouter load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new IFunctionsRouter(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<IFunctionsRouter> deploy(Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        return deployRemoteCall(IFunctionsRouter.class, web3j, credentials, contractGasProvider, getDeploymentBinary(), "");
    }

    @Deprecated
    public static RemoteCall<IFunctionsRouter> deploy(Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(IFunctionsRouter.class, web3j, credentials, gasPrice, gasLimit, getDeploymentBinary(), "");
    }

    public static RemoteCall<IFunctionsRouter> deploy(Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(IFunctionsRouter.class, web3j, transactionManager, contractGasProvider, getDeploymentBinary(), "");
    }

    @Deprecated
    public static RemoteCall<IFunctionsRouter> deploy(Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(IFunctionsRouter.class, web3j, transactionManager, gasPrice, gasLimit, getDeploymentBinary(), "");
    }

    public static void linkLibraries(List<Contract.LinkReference> references) {
        librariesLinkedBinary = linkBinaryWithReferences(BINARY, references);
    }

    private static String getDeploymentBinary() {
        if (librariesLinkedBinary != null) {
            return librariesLinkedBinary;
        } else {
            return BINARY;
        }
    }

    public static class Commitment extends StaticStruct {
        public byte[] requestId;

        public String coordinator;

        public BigInteger estimatedTotalCostJuels;

        public String client;

        public BigInteger subscriptionId;

        public BigInteger callbackGasLimit;

        public BigInteger adminFee;

        public BigInteger donFee;

        public BigInteger gasOverheadBeforeCallback;

        public BigInteger gasOverheadAfterCallback;

        public BigInteger timeoutTimestamp;

        public Commitment(byte[] requestId, String coordinator, BigInteger estimatedTotalCostJuels,
                String client, BigInteger subscriptionId, BigInteger callbackGasLimit,
                BigInteger adminFee, BigInteger donFee, BigInteger gasOverheadBeforeCallback,
                BigInteger gasOverheadAfterCallback, BigInteger timeoutTimestamp) {
            super(new org.web3j.abi.datatypes.generated.Bytes32(requestId), 
                    new org.web3j.abi.datatypes.Address(160, coordinator), 
                    new org.web3j.abi.datatypes.generated.Uint96(estimatedTotalCostJuels), 
                    new org.web3j.abi.datatypes.Address(160, client), 
                    new org.web3j.abi.datatypes.generated.Uint64(subscriptionId), 
                    new org.web3j.abi.datatypes.generated.Uint32(callbackGasLimit), 
                    new org.web3j.abi.datatypes.generated.Uint72(adminFee), 
                    new org.web3j.abi.datatypes.generated.Uint72(donFee), 
                    new org.web3j.abi.datatypes.generated.Uint40(gasOverheadBeforeCallback), 
                    new org.web3j.abi.datatypes.generated.Uint40(gasOverheadAfterCallback), 
                    new org.web3j.abi.datatypes.generated.Uint32(timeoutTimestamp));
            this.requestId = requestId;
            this.coordinator = coordinator;
            this.estimatedTotalCostJuels = estimatedTotalCostJuels;
            this.client = client;
            this.subscriptionId = subscriptionId;
            this.callbackGasLimit = callbackGasLimit;
            this.adminFee = adminFee;
            this.donFee = donFee;
            this.gasOverheadBeforeCallback = gasOverheadBeforeCallback;
            this.gasOverheadAfterCallback = gasOverheadAfterCallback;
            this.timeoutTimestamp = timeoutTimestamp;
        }

        public Commitment(Bytes32 requestId, Address coordinator, Uint96 estimatedTotalCostJuels,
                Address client, Uint64 subscriptionId, Uint32 callbackGasLimit, Uint72 adminFee,
                Uint72 donFee, Uint40 gasOverheadBeforeCallback, Uint40 gasOverheadAfterCallback,
                Uint32 timeoutTimestamp) {
            super(requestId, coordinator, estimatedTotalCostJuels, client, subscriptionId, callbackGasLimit, adminFee, donFee, gasOverheadBeforeCallback, gasOverheadAfterCallback, timeoutTimestamp);
            this.requestId = requestId.getValue();
            this.coordinator = coordinator.getValue();
            this.estimatedTotalCostJuels = estimatedTotalCostJuels.getValue();
            this.client = client.getValue();
            this.subscriptionId = subscriptionId.getValue();
            this.callbackGasLimit = callbackGasLimit.getValue();
            this.adminFee = adminFee.getValue();
            this.donFee = donFee.getValue();
            this.gasOverheadBeforeCallback = gasOverheadBeforeCallback.getValue();
            this.gasOverheadAfterCallback = gasOverheadAfterCallback.getValue();
            this.timeoutTimestamp = timeoutTimestamp.getValue();
        }
    }
}
