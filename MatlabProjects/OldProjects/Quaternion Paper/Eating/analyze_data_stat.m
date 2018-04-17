sub_count = length(data);
res = zeros(sub_count, 3);

for i = 1:12
    d = data(i).data;    
    annots = data(i).annots;
    bites = annots(annots(:, 3)<=7, 1);
    drinks = annots(annots(:, 3)>7, 1);
    res(i, :) = [length(annots), length(bites), length(drinks)];    
end

res = [res; sum(res)]