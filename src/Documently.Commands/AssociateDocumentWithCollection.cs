using System;

namespace Documently.Commands
{
    [Serializable]
    public class AssociateDocumentWithCollection : Command
    {
        public Guid CollectionId { get; protected set; }

        public AssociateDocumentWithCollection()
        {}

        public AssociateDocumentWithCollection(Guid docId, Guid collectionId) : base(docId)
        {
            CollectionId = collectionId;
        }
    }
}