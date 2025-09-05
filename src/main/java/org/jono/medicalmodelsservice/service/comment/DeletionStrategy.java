package org.jono.medicalmodelsservice.service.comment;

interface DeletionStrategy {
    CommentsToDelete execute();
}
