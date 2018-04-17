close all;
a=2/(1+sqrt(5));

M =[
0,a,-1,a,1,0,-a,1,0;
0,a,1,-a,1,0,a,1,0;
0,a,1,0,-a,1,-1,0,a;
0,a,1,1,0,a,0,-a,1;
0,a,-1,0,-a,-1,1,0,-a;
0,a,-1,-1,0,-a,0,-a,-1;
0,-a,1,a,-1,0,-a,-1,0;
0,-a,-1,-a,-1,0,a,-1,0;
-a,1,0,-1,0,a,-1,0,-a;
-a,-1,0,-1,0,-a,-1,0,a;
a,1,0,1,0,-a,1,0,a;
a,-1,0,1,0,a,1,0,-a;
0,a,1,-1,0,a,-a,1,0;
0,a,1,a,1,0,1,0,a;
0,a,-1,-a,1,0,-1,0,-a;
0,a,-1,1,0,-a,a,1,0;
0,-a,-1,-1,0,-a,-a,-1,0;
0,-a,-1,a,-1,0,1,0,-a;
0,-a,1,-a,-1,0,-1,0,a;
0,-a,1,1,0,a,a,-1,0
];

r = norm([0,1,a])
M = M/r;
norm(M(1,1:3))

res = 3;
for i = 1:res
    v = [];
    len = length(M)
    for j =1:len
        v1 = M(j, 1:3);
        v2 = M(j, 4:6);
        v3 = M(j, 7:9);
        
        v12 = (v1+v2)/2;
        v23 = (v2+v3)/2;
        v31 = (v3+v1)/2;
        
        v12 = v12/norm(v12);
        v23 = v23/norm(v23);
        v31 = v31/norm(v31);
        
        v = [v; v1, v12, v31; v2, v12, v23; v3, v31, v23; v12, v23, v31];
    end
    
    M = v;
end



figure;
r = 0.95;
[x,y,z] = sphere(20);
mesh(x*r, y*r, z*r, 'FaceColor',[0.95 0.95 0.95], 'LineStyle','none'); 
hold on;

dist=[];
len = length(M)
for i = 1:len    
    v1 = M(i,1:3);
    v2 = M(i, 4:6);
    v3 = M(i, 7:9);
    a = [v1; v2; v3; v1];    
    plot3(a(:,1), a(:,2), a(:,3));
    
    d1 = norm(v1-v2);
    d2 = norm(v2-v3);
    d3 = norm(v3-v1);
    dist = [dist; d1; d2; d3]; 
end

M = [M(:,1:3);M(:,4:6);M(:,7:9)];
M = unique(M,'rows');
size(M)
a = M;
scatter3(a(:,1), a(:, 2), a(:, 3));

min(dist)
max(dist)
unique(dist)

save('M','M');
