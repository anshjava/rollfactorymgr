package ru.kamuzta.rollfactorymgr.processor;

import com.google.inject.ImplementedBy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.kamuzta.rollfactorymgr.exception.ValidationException;
import ru.kamuzta.rollfactorymgr.exception.WebServiceException;
import ru.kamuzta.rollfactorymgr.model.client.Client;

import java.time.LocalDate;
import java.util.List;

@ImplementedBy(ClientProcessorImpl.class)
public interface ClientProcessor {

    /**
     * Update local cached Registry by values from server
     *
     * @throws WebServiceException on connection problems
     */
    void updateRegistryFromServer() throws WebServiceException;

    /**
     * Get active clients from local cached Registry
     *
     * @return active clients
     * @throws WebServiceException on connection problems
     */
    List<Client> getActiveClientsLocal();

    /**
     * Find Client by id in local cached Client Registry
     *
     * @param id id of Client
     * @return found Client
     * @throws WebServiceException on connection problems
     */
    Client findClientById(@NotNull Long id) throws WebServiceException;

    /**
     * Find Client by companyName pattern in local cached Client Registry
     *
     * @param companyName name/part of name of Client
     * @return list of matched Clients
     * @throws WebServiceException on connection problems
     */
    List<Client> findClientByNamePattern(@NotNull String companyName) throws WebServiceException;

    /**
     * Find Client by parameters in local cached Client Registry
     *
     * @param id               part of client id
     * @param companyName      part of client's company name
     * @param creationDateFrom client creation date from
     * @param creationDateTo   client creation date to
     * @param city             part of city
     * @param address          part of address
     * @param buyerName        part of client's buyer name
     * @param phone            part of client's buyer phone
     * @param email            part of client's buyer email
     * @return list of matched Clients
     * @throws WebServiceException on connection problems
     */
    List<Client> findClientByParams(@Nullable Long id, @Nullable String companyName, @Nullable LocalDate creationDateFrom, @Nullable LocalDate creationDateTo,
                                    @Nullable String city, @Nullable String address, @Nullable String buyerName, @Nullable String phone, @Nullable String email) throws WebServiceException;

    /**
     * Create Client on Server Registry
     *
     * @param creationDate dateTime of creation
     * @param companyName  client's company name
     * @param city         city
     * @param address      address
     * @param buyerName    client's buyer name
     * @param phone        client's buyer phone
     * @param email        client's buyer email
     * @return new Client
     * @throws WebServiceException on connection problems or remote validation fail
     * @throws ValidationException if local validation fail
     */
    Client createClient(@Nullable LocalDate creationDate, @NotNull String companyName, @NotNull String city,
                        @NotNull String address, @NotNull String buyerName, @NotNull String phone, @NotNull String email) throws WebServiceException, ValidationException;

    /**
     * Remove Client on Server Registry by id
     *
     * @param id id of Client to remove
     * @return true if success
     * @throws WebServiceException on connection problems or remote validation fail
     * @throws ValidationException if Client with specified id was not found or there is some orders by this client
     */
    boolean removeClientById(@NotNull Long id) throws WebServiceException, ValidationException;

    /**
     * Update Client on Server Registry with new parameters
     *
     * @param client - Client with same id but diffirent parameters
     * @return updated Client
     * @throws WebServiceException on connection problems or remote validation fail
     * @throws ValidationException if local validation fail or all parameters are equals
     */
    Client updateClient(@NotNull Client client) throws WebServiceException, ValidationException;

    void validateCreateClient(@Nullable LocalDate creationDate, @NotNull String companyName, @NotNull String city, @NotNull String address,
                              @NotNull String buyerName, @NotNull String phone, @NotNull String email) throws ValidationException;
    void validateUpdateClient(Client client) throws ValidationException;
    void validateRemoveClient(Long id) throws ValidationException;
    void validateCommonClientParams(String city, String address, String buyerName, String phone, String email) throws ValidationException;
    void validateIfClientInWorkflow(Long id) throws ValidationException;

}
