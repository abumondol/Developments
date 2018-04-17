load('data');
sub_count = 5;

all_annots=[];
for sub=1:sub_count
    for sess = 1:2
        annots = data(sub).session(sess).annotations;        
        len = size(annots,1);
        sub_id = zeros(len,1) + sub;
        all_annots = [all_annots; sub_id, annots(:,2)]; 
    end
end

labels = unique(all_annots(:,2));
label_type_count = length(labels);
stat_labels = zeros(label_type_count, sub_count);

for sub = 1:sub_count
    for label = 1:label_type_count
        stat_labels(label, sub) = sum(all_annots(:,1) == sub & all_annots(:,2) == labels(label));
    end
end
stat_labels = [stat_labels, sum(stat_labels, 2)];  
stat_labels = [stat_labels; sum(stat_labels, 1)];  
stat_labels = [[labels;0], stat_labels]; 
