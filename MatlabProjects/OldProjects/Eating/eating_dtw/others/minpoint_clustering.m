data_count = length(data)

pos = [];
neg = [];
pos_id = [];
neg_id = [];

for s = 1:data_count
    accel = data(s).accel(:, 2:4);
    annot = data(s).annots;
    
    if isempty(annot)
        mp = segments(s).min_points;
        sub_id = zeros(length(mp),1)+s;
        neg_id = [neg_id; sub_id];
        neg = [neg; accel(mp, :)];
        continue
    end
    
    seg = [segments(s).min_points, segments(s).labels];
    posix = seg(seg(:,2)>0 & seg(:,2)<400 & seg(:,4)<32, 1);
    negix = seg(seg(:,2)==0, 1);
    
    sub_id = zeros(length(posix),1)+s;
    pos_id = [pos_id; sub_id];
    pos = [pos; accel(posix, :)];
    
    sub_id = zeros(length(negix),1)+s;
    neg_id = [neg_id; sub_id];
    neg = [neg; accel(negix, :)];
end

pc = pca(pos);
p = pos*pc;
n = neg*pc;

figure
hist(p(:, 1))
figure
hist(p(:, 2))
figure
hist(p(:, 3))
p = n;
figure
hist(p(:, 1))
figure
hist(p(:, 2))
figure
hist(p(:, 3))

return
pos = mynormalize(pos);
neg = mynormalize(neg);
k = 50;
res = zeros(k, 4);
[idx, Ctr] = kmedoids(pos, k);
for i=1:k
    p = pos(idx==i, :);
    c = Ctr(i, :);
    c = repmat(c, size(p, 1), 1);
    d = p-c;
    d = sum(d.*d, 2);
    res(i,1) = length(d);
    res(i, 2) = max(d);
    
    c = Ctr(i, :);
    c = repmat(c, size(neg, 1), 1);
    d = neg-c;
    d = sum(d.*d, 2);    
    res(i,3) = sum(d<=res(i,2));
    res(i,4) = sum(d<=1.1*res(i,2));
end

%res = [res(:, 1)./res(:, 2), res];
res = sortrows(res, 1);
res = flipud(res)
sum(res)
length(neg)


C = 1.03*C;
figure;
r = 0.97;
[x,y,z] = sphere(20);
mesh(x*r, y*r, z*r, 'FaceColor',[0.95 0.95 0.95], 'LineStyle','none'); 
hold on;

a = neg;
scatter3(a(:,1), a(:,2), a(:,3), 'rx');
hold on;
a = pos;
scatter3(a(:,1), a(:,2), a(:,3), 'bo');
a = C;
scatter3(a(:,1), a(:,2), a(:,3), 'go');
