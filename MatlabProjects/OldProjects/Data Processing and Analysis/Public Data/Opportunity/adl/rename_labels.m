function labels = rename_labels(labels)
    labels(labels==402, 1) = 1;
    labels(labels==408, 1) = 2;
    labels(labels==410, 1) = 3;
    labels(labels==411, 1) = 4;
    labels(labels>=5, 1) = 0;
end