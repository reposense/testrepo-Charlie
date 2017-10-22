package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;
import java.util.Set;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.model.tag.Tag;

/**
 * Changes the privacy setting of a person's details in the address book
 */
public class ChangePrivacyCommand extends UndoableCommand {
    public static final String COMMAND_WORD = "changeprivacy";
    public static final String COMMAND_ALIAS = "cp";

    public static final String TRUE_WORD = "true";
    public static final String FALSE_WORD = "false";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Changes the privacy of the details of the person"
            + "identified by the index number used in the last person listing.\n "
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + TRUE_WORD + " OR " + FALSE_WORD + "]"
            + "[" + PREFIX_PHONE + TRUE_WORD + " OR " + FALSE_WORD + "]"
            + "[" + PREFIX_EMAIL + TRUE_WORD + " OR " + FALSE_WORD + "]"
            + "[" + PREFIX_ADDRESS + TRUE_WORD + " OR " + FALSE_WORD + "]"
            + "[" + PREFIX_TAG + TRUE_WORD + " OR " + FALSE_WORD + "]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + TRUE_WORD
            + PREFIX_EMAIL + FALSE_WORD;

    public static final String MESSAGE_CHANGE_PRIVACY_SUCCESS = "Changed the Privacy of the Person: %1$s";
    public static final String MESSAGE_NO_FIELDS = "At least one field to change must be provided.";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";

    private final Index index;
    private final PersonPrivacySettings pps;

    /**
     * @param index of the person in the filtered person list to change the privacy of
     */
    public ChangePrivacyCommand(Index index, PersonPrivacySettings pps) {
        requireNonNull(index);
        requireNonNull(pps);

        this.index = index;
        this.pps = pps;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        ReadOnlyPerson personToChange = lastShownList.get(index.getZeroBased());

        Person newPerson = createPersonWithChangedPrivacy(personToChange, pps);

        try {
            model.updatePerson(personToChange, newPerson);
        } catch (DuplicatePersonException dpe) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        } catch (PersonNotFoundException pnfe) {
            throw new AssertionError("The target person cannot be missing");
        }

        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_CHANGE_PRIVACY_SUCCESS, newPerson));
    }

    /**
     * Changes a person's fields' privacy
     * @param person the person whose privacy we would like to change
     * @param pps the settings of privacy for each field
     */
    private static Person createPersonWithChangedPrivacy(ReadOnlyPerson person, PersonPrivacySettings pps) {
        assert person != null;

        Name n = person.getName();
        Phone p = person.getPhone();
        Email e = person.getEmail();
        Address a = person.getAddress();
        Set<Tag> t = person.getTags( );

        if (pps.nameIsPrivate() != null) {
            n.setPrivate(pps.nameIsPrivate());
        }
        if (pps.phoneIsPrivate() != null) {
            p.setPrivate(pps.phoneIsPrivate());
        }

        if (pps.emailIsPrivate() != null) {
            e.setPrivate(pps.emailIsPrivate());
        }

        if (pps.addressIsPrivate() != null) {
            a.setPrivate(pps.addressIsPrivate());
        }

        return new Person(n, p, e, a, t);
    }

    /**
     * Stores the privacy settings for each field of a person.
     */
    public static class PersonPrivacySettings {
        private Boolean nameIsPrivate;
        private Boolean phoneIsPrivate;
        private Boolean emailIsPrivate;
        private Boolean addressIsPrivate;

        /**
         * Returns true if at least one field is not null.
         */
        public boolean isAnyFieldNonNull() {
            return CollectionUtil.isAnyNonNull(this.nameIsPrivate, this.phoneIsPrivate,
                    this.emailIsPrivate, this.addressIsPrivate);
        }

        public Boolean nameIsPrivate() {
            return nameIsPrivate;
        }

        public void setNameIsPrivate(boolean nameIsPrivate) {
            requireNonNull(nameIsPrivate);
            this.nameIsPrivate = nameIsPrivate;
        }

        public Boolean phoneIsPrivate() {
            return phoneIsPrivate;
        }

        public void setPhoneIsPrivate(boolean phoneIsPrivate) {
            requireNonNull(phoneIsPrivate);
            this.phoneIsPrivate = phoneIsPrivate;
        }

        public Boolean emailIsPrivate() {
            return emailIsPrivate;
        }

        public void setEmailIsPrivate(boolean emailIsPrivate) {
            requireNonNull(emailIsPrivate);
            this.emailIsPrivate = emailIsPrivate;
        }

        public Boolean addressIsPrivate() {
            return addressIsPrivate;
        }

        public void setAddressIsPrivate(boolean addressIsPrivate) {
            requireNonNull(addressIsPrivate);
            this.addressIsPrivate = addressIsPrivate;
        }
    }
}
