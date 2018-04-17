sub_count = length(data);
labels = [1:7, 101:104, 201, 301:309, 401, 501:504, 601];
labels = labels(labels>400);
edges = -12:3:0
label_count = length(labels);
edges_count = length(edges);

res = zeros(edges_count-1, label_count);

x = [];
for i = 1:label_count
    label = labels(i);
    %x = [];
    for sub = 1:34    
        accel = data(sub).accel;
        a = data(sub).annots;
        a = get_annot_indices(accel, a);
        a = a(a(:, 2)==label, 1);
        x = [x; accel(a, 2)];
    end
    %[N, ~] = histcounts(x, edges);    
    %res(:, i) = flip(N);    
end
[res, ~] = histcounts(x, edges);            

%res = sum(res, 2);
%edges = flip(edges);
res = [edges(1:end-1)', edges(2:end)', res']
