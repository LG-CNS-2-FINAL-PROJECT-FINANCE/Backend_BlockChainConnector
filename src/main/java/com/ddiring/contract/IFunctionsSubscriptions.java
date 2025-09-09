package com.ddiring.contract;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.DynamicStruct;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.StaticStruct;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint32;
import org.web3j.abi.datatypes.generated.Uint40;
import org.web3j.abi.datatypes.generated.Uint64;
import org.web3j.abi.datatypes.generated.Uint72;
import org.web3j.abi.datatypes.generated.Uint96;
import org.web3j.abi.datatypes.reflection.Parameterized;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
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
public class IFunctionsSubscriptions extends Contract {
    public static final String BINARY = "";

    private static String librariesLinkedBinary;

    public static final String FUNC_ACCEPTSUBSCRIPTIONOWNERTRANSFER = "acceptSubscriptionOwnerTransfer";

    public static final String FUNC_ADDCONSUMER = "addConsumer";

    public static final String FUNC_CANCELSUBSCRIPTION = "cancelSubscription";

    public static final String FUNC_CREATESUBSCRIPTION = "createSubscription";

    public static final String FUNC_CREATESUBSCRIPTIONWITHCONSUMER = "createSubscriptionWithConsumer";

    public static final String FUNC_GETCONSUMER = "getConsumer";

    public static final String FUNC_GETFLAGS = "getFlags";

    public static final String FUNC_GETSUBSCRIPTION = "getSubscription";

    public static final String FUNC_GETSUBSCRIPTIONCOUNT = "getSubscriptionCount";

    public static final String FUNC_GETSUBSCRIPTIONSINRANGE = "getSubscriptionsInRange";

    public static final String FUNC_GETTOTALBALANCE = "getTotalBalance";

    public static final String FUNC_ORACLEWITHDRAW = "oracleWithdraw";

    public static final String FUNC_OWNERCANCELSUBSCRIPTION = "ownerCancelSubscription";

    public static final String FUNC_PENDINGREQUESTEXISTS = "pendingRequestExists";

    public static final String FUNC_PROPOSESUBSCRIPTIONOWNERTRANSFER = "proposeSubscriptionOwnerTransfer";

    public static final String FUNC_RECOVERFUNDS = "recoverFunds";

    public static final String FUNC_REMOVECONSUMER = "removeConsumer";

    public static final String FUNC_SETFLAGS = "setFlags";

    public static final String FUNC_TIMEOUTREQUESTS = "timeoutRequests";

    @Deprecated
    protected IFunctionsSubscriptions(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected IFunctionsSubscriptions(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected IFunctionsSubscriptions(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected IFunctionsSubscriptions(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<TransactionReceipt> acceptSubscriptionOwnerTransfer(
            BigInteger subscriptionId) {
        final Function function = new Function(
                FUNC_ACCEPTSUBSCRIPTIONOWNERTRANSFER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint64(subscriptionId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> addConsumer(BigInteger subscriptionId,
            String consumer) {
        final Function function = new Function(
                FUNC_ADDCONSUMER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint64(subscriptionId), 
                new org.web3j.abi.datatypes.Address(160, consumer)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> cancelSubscription(BigInteger subscriptionId,
            String to) {
        final Function function = new Function(
                FUNC_CANCELSUBSCRIPTION, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint64(subscriptionId), 
                new org.web3j.abi.datatypes.Address(160, to)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> createSubscription() {
        final Function function = new Function(
                FUNC_CREATESUBSCRIPTION, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> createSubscriptionWithConsumer(String consumer) {
        final Function function = new Function(
                FUNC_CREATESUBSCRIPTIONWITHCONSUMER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, consumer)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Consumer> getConsumer(String client, BigInteger subscriptionId) {
        final Function function = new Function(FUNC_GETCONSUMER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, client), 
                new org.web3j.abi.datatypes.generated.Uint64(subscriptionId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Consumer>() {}));
        return executeRemoteCallSingleValueReturn(function, Consumer.class);
    }

    public RemoteFunctionCall<byte[]> getFlags(BigInteger subscriptionId) {
        final Function function = new Function(FUNC_GETFLAGS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint64(subscriptionId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<Subscription> getSubscription(BigInteger subscriptionId) {
        final Function function = new Function(FUNC_GETSUBSCRIPTION, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint64(subscriptionId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Subscription>() {}));
        return executeRemoteCallSingleValueReturn(function, Subscription.class);
    }

    public RemoteFunctionCall<BigInteger> getSubscriptionCount() {
        final Function function = new Function(FUNC_GETSUBSCRIPTIONCOUNT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint64>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<List> getSubscriptionsInRange(BigInteger subscriptionIdStart,
            BigInteger subscriptionIdEnd) {
        final Function function = new Function(FUNC_GETSUBSCRIPTIONSINRANGE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint64(subscriptionIdStart), 
                new org.web3j.abi.datatypes.generated.Uint64(subscriptionIdEnd)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Subscription>>() {}));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<BigInteger> getTotalBalance() {
        final Function function = new Function(FUNC_GETTOTALBALANCE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint96>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> oracleWithdraw(String recipient,
            BigInteger amount) {
        final Function function = new Function(
                FUNC_ORACLEWITHDRAW, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, recipient), 
                new org.web3j.abi.datatypes.generated.Uint96(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> ownerCancelSubscription(
            BigInteger subscriptionId) {
        final Function function = new Function(
                FUNC_OWNERCANCELSUBSCRIPTION, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint64(subscriptionId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Boolean> pendingRequestExists(BigInteger subscriptionId) {
        final Function function = new Function(FUNC_PENDINGREQUESTEXISTS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint64(subscriptionId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<TransactionReceipt> proposeSubscriptionOwnerTransfer(
            BigInteger subscriptionId, String newOwner) {
        final Function function = new Function(
                FUNC_PROPOSESUBSCRIPTIONOWNERTRANSFER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint64(subscriptionId), 
                new org.web3j.abi.datatypes.Address(160, newOwner)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> recoverFunds(String to) {
        final Function function = new Function(
                FUNC_RECOVERFUNDS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, to)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> removeConsumer(BigInteger subscriptionId,
            String consumer) {
        final Function function = new Function(
                FUNC_REMOVECONSUMER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint64(subscriptionId), 
                new org.web3j.abi.datatypes.Address(160, consumer)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setFlags(BigInteger subscriptionId,
            byte[] flags) {
        final Function function = new Function(
                FUNC_SETFLAGS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint64(subscriptionId), 
                new org.web3j.abi.datatypes.generated.Bytes32(flags)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> timeoutRequests(
            List<Commitment> requestsToTimeoutByCommitment) {
        final Function function = new Function(
                FUNC_TIMEOUTREQUESTS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<Commitment>(Commitment.class, requestsToTimeoutByCommitment)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static IFunctionsSubscriptions load(String contractAddress, Web3j web3j,
            Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new IFunctionsSubscriptions(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static IFunctionsSubscriptions load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new IFunctionsSubscriptions(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static IFunctionsSubscriptions load(String contractAddress, Web3j web3j,
            Credentials credentials, ContractGasProvider contractGasProvider) {
        return new IFunctionsSubscriptions(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static IFunctionsSubscriptions load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new IFunctionsSubscriptions(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<IFunctionsSubscriptions> deploy(Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        return deployRemoteCall(IFunctionsSubscriptions.class, web3j, credentials, contractGasProvider, getDeploymentBinary(), "");
    }

    @Deprecated
    public static RemoteCall<IFunctionsSubscriptions> deploy(Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(IFunctionsSubscriptions.class, web3j, credentials, gasPrice, gasLimit, getDeploymentBinary(), "");
    }

    public static RemoteCall<IFunctionsSubscriptions> deploy(Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(IFunctionsSubscriptions.class, web3j, transactionManager, contractGasProvider, getDeploymentBinary(), "");
    }

    @Deprecated
    public static RemoteCall<IFunctionsSubscriptions> deploy(Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(IFunctionsSubscriptions.class, web3j, transactionManager, gasPrice, gasLimit, getDeploymentBinary(), "");
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

    public static class Consumer extends StaticStruct {
        public Boolean allowed;

        public BigInteger initiatedRequests;

        public BigInteger completedRequests;

        public Consumer(Boolean allowed, BigInteger initiatedRequests,
                BigInteger completedRequests) {
            super(new org.web3j.abi.datatypes.Bool(allowed), 
                    new org.web3j.abi.datatypes.generated.Uint64(initiatedRequests), 
                    new org.web3j.abi.datatypes.generated.Uint64(completedRequests));
            this.allowed = allowed;
            this.initiatedRequests = initiatedRequests;
            this.completedRequests = completedRequests;
        }

        public Consumer(Bool allowed, Uint64 initiatedRequests, Uint64 completedRequests) {
            super(allowed, initiatedRequests, completedRequests);
            this.allowed = allowed.getValue();
            this.initiatedRequests = initiatedRequests.getValue();
            this.completedRequests = completedRequests.getValue();
        }
    }

    public static class Subscription extends DynamicStruct {
        public BigInteger balance;

        public String owner;

        public BigInteger blockedBalance;

        public String proposedOwner;

        public List<String> consumers;

        public byte[] flags;

        public Subscription(BigInteger balance, String owner, BigInteger blockedBalance,
                String proposedOwner, List<String> consumers, byte[] flags) {
            super(new org.web3j.abi.datatypes.generated.Uint96(balance), 
                    new org.web3j.abi.datatypes.Address(160, owner), 
                    new org.web3j.abi.datatypes.generated.Uint96(blockedBalance), 
                    new org.web3j.abi.datatypes.Address(160, proposedOwner), 
                    new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                            org.web3j.abi.datatypes.Address.class,
                            org.web3j.abi.Utils.typeMap(consumers, org.web3j.abi.datatypes.Address.class)), 
                    new org.web3j.abi.datatypes.generated.Bytes32(flags));
            this.balance = balance;
            this.owner = owner;
            this.blockedBalance = blockedBalance;
            this.proposedOwner = proposedOwner;
            this.consumers = consumers;
            this.flags = flags;
        }

        public Subscription(Uint96 balance, Address owner, Uint96 blockedBalance,
                Address proposedOwner,
                @Parameterized(type = Address.class) DynamicArray<Address> consumers,
                Bytes32 flags) {
            super(balance, owner, blockedBalance, proposedOwner, consumers, flags);
            this.balance = balance.getValue();
            this.owner = owner.getValue();
            this.blockedBalance = blockedBalance.getValue();
            this.proposedOwner = proposedOwner.getValue();
            this.consumers = consumers.getValue().stream().map(v -> v.getValue()).collect(Collectors.toList());
            this.flags = flags.getValue();
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
