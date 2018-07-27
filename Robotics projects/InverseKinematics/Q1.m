% TODO: You write this function!
% input: f -> an 9-joint robot encoded as a SerialLink class
%        position -> 3x1 vector denoting the target position to move to
% output: q -> 1x9 vector of joint angles that cause the end
%              effector position to reach <position>
%              (orientation is to be ignored)
function q = Q1(f,position)

 % homogeneous transformation matrix for linear displacement
 t = [1 0 0 position(1,1);0 1 0 position(2,1); 0 0 1 position(3,1); 0 0 0 1];
 
 %using forward kinematics to fetch new angles
 q = f.ikine(t);
 
end
