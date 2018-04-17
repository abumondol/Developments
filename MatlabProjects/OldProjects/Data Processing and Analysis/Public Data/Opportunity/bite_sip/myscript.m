clear; load('..\oppdata');
window_size = 6*30;
[indices, baseline_labels] = get_bite_sip_indices(oppdata, window_size);
save('indices','indices');

features = baseline_features(oppdata, baseline_labels, window_size);
baseline = [];
baseline.labels = baseline_labels;
baseline.features = features;
save('baseline','baseline');




