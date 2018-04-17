function res =normalize(data)
mag = sqrt(sum(data.*data, 2));
res = data./[mag mag mag];
end